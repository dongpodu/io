package com.elisonwell.io;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * 文件工具类 
 * 封装文件操作逻辑，在调用之前需在环境对应的配置文件配置file.baseDir
 * @author duyisong
 */
public class FileKit {

	private static String BASE_DIR = "/Users/duyisong/Downloads/io";
	public static final String TEMP_DIR = "temp/"; // 临时文件夹
	public static final String UN_ACTIVE_DIR = "un-act/"; // 未激活文件
	public static final String PERM_DIR = "perm/"; // 永久文件夹（存放已激活文件）
	public static final String FILE_SPERATOR = "/";

	/**
	 * 获取文件
	 */
	public static File getFile(String path) {
		if (isBlank(path)) {
			return null;
		}

		File file = new File(getAbsolutePath(path));
		if (!file.exists()) {
			throw new IllegalArgumentException("不存在对应的文件！" + path);
		}
		return file;
	}

	/**
	 * 删除文件
	 */
	public static boolean deleteFile(String path) {
		File file = new File(getAbsolutePath(path));
		if (!file.exists()) {
			throw new IllegalArgumentException("不存在对应的文件！" + path);
		}
		return file.delete();
	}

	/**
	 * 激活文件，将文件从non-act文件夹移到perm文件夹下 path以un-act字符串开头 如un-act/2016/1/3/text.txt
	 * 
	 * @param path
	 * @return
	 */
	public static String activeFile(String path) {
		if (isBlank(path)) {
			return null;
		}

		if (path.startsWith(TEMP_DIR)) {
			throw new IllegalArgumentException("不能激活临时文件！" + path);
		}

		if (path.startsWith(PERM_DIR)) {
			return path;
		}

		String permPath = path.replace(UN_ACTIVE_DIR, PERM_DIR);
		File unActiveFile = new File(getAbsolutePath(path));
		File permFile = new File(getAbsolutePath(permPath));
		if (!unActiveFile.exists() && !permFile.exists()) {
			throw new IllegalArgumentException("不存在对应的文件，无法激活！" + path);
		}
		boolean success = move(path, permPath);
		if (success) {
			return permPath;
		}
		return null;
	}

	/**
	 * 保存文件到未激活目录
	 * 
	 * @param file
	 * @param suffix
	 * @return 文件路径
	 * @throws IOException
	 */
	public static String saveFile(File file, String suffix) throws IOException {
		if (file == null) {
			return null;
		}
		return saveFile(new FileInputStream(file), suffix);
	}
	
	/**
	 * 保存文件到临时目录
	 * 
	 * @param file
	 * @param suffix
	 * @return 文件路径
	 * @throws IOException
	 */
	public static String saveFileToTempDir(File file, String suffix) throws IOException {
		if (file == null || isBlank(suffix)) {
			return null;
		}
		// 保存在临时目录
		long nanoTimes = System.nanoTime();
		String fileName = nanoTimes + "." + suffix.toLowerCase();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dir = TEMP_DIR + year + FILE_SPERATOR + month + FILE_SPERATOR + day
				+ FILE_SPERATOR;
		
		saveFile(new FileInputStream(file), fileName, dir);
		return dir+fileName;
	}

	/**
	 * 读取输入流并保存到未激活目录
	 * 
	 * @param input
	 * @param suffix
	 * @return 文件路径
	 * @throws IOException
	 */
	public static String saveFile(InputStream input, String suffix)
			throws IOException {

		if (input == null || isBlank(suffix)) {
			return null;
		}

		// 保存在临时目录
		long nanoTimes = System.nanoTime();
		String fileName = nanoTimes + "." + suffix.toLowerCase();
		String sourcePath = TEMP_DIR + fileName;
		saveFile(input, fileName, TEMP_DIR);

		// 移动到永久目录
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dir = year + FILE_SPERATOR + month + FILE_SPERATOR + day
				+ FILE_SPERATOR;
		String destPath = UN_ACTIVE_DIR + dir + fileName;
		move(sourcePath, destPath);

		return destPath;

	}

	/**
	 * 保存字节到未激活目录
	 * 
	 * @param bytes
	 * @param suffix
	 * @return 文件地址
	 * @throws IOException
	 */
	public static String saveFile(byte[] bytes, String suffix)
			throws IOException {

		if (bytes == null || bytes.length == 0 || isBlank(suffix)) {
			return null;
		}

		// 保存在临时目录
		long nanoTimes = System.nanoTime();
		String fileName = nanoTimes + "." + suffix.toLowerCase();
		String sourcePath = TEMP_DIR + fileName;
		saveFile(bytes, fileName, TEMP_DIR);

		// 移动到未激活目录
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dir = year + FILE_SPERATOR + month + FILE_SPERATOR + day
				+ FILE_SPERATOR;
		String destPath = UN_ACTIVE_DIR + dir + fileName;
		move(sourcePath, destPath);

		return destPath;

	}

	/**
	 * 保存文件到未激活目录
	 * 
	 * @param input
	 *            输入流
	 * @param fileName
	 *            如test.apk
	 * @param dir
	 *            不以'/'开头，但以'/'结尾，如apk/123/123456/
	 * @throws IOException
	 * @return 文件大小，单位字节
	 */
	private static int saveFile(InputStream input, String fileName, String dir)
			throws IOException {
		if (input == null || isBlank(fileName)
				|| isBlank(dir)) {
			return 0;
		}

		checkDirExists(dir);

		byte[] buffer = new byte[8 * 1024];
		int len = 0, size = 0;
		String uploadPath = getAbsolutePath(fileName, dir);
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(uploadPath));
		while ((len = input.read(buffer)) != -1) {
			out.write(buffer, 0, len);
			size += len;
		}
		out.close();
		input.close();
		return size;
	}

	/**
	 * 保存文件到指定地址
	 * 
	 * @param input
	 * @param savedPath
	 * @return
	 * @throws IOException
	 */
	public static int saveFile1(InputStream input, String savedPath)
			throws IOException {
		if (input == null || isBlank(savedPath)) {
			return 0;
		}

		String uploadPath = getAbsolutePath(savedPath);
		String dir = uploadPath.substring(0,
				uploadPath.lastIndexOf(FILE_SPERATOR));

		checkDirExists(dir);

		byte[] buffer = new byte[8 * 1024];
		int len = 0, size = 0;
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(uploadPath));
		while ((len = input.read(buffer)) != -1) {
			out.write(buffer, 0, len);
			size += len;
		}
		out.close();
		input.close();
		return size;
	}

	/**
	 * @param dir
	 *            不以'/'开头，但以'/'结尾，如apk/123/123456/
	 */
	private static void checkDirExists(String dir) {
		File file = new File(BASE_DIR + FILE_SPERATOR + dir);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * @param data
	 *            文件字节
	 * @param fileName
	 *            保存的文件名 如test.apk
	 * @param dir
	 *            不以'/'开头，但以'/'结尾，如apk/123/123456/
	 * @throws IOException
	 */
	private static void saveFile(byte[] data, String fileName, String dir)
			throws IOException {
		if (data == null || isBlank(fileName)
				|| isBlank(dir)) {
			return;
		}

		checkDirExists(dir);

		String uploadPath = getAbsolutePath(fileName, dir);
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(uploadPath));
		out.write(data);
		out.close();
	}

	/**
	 * 重命名
	 * 
	 * @param source
	 *            源文件路径，如apk/123/123456/test.apk
	 * @param dest
	 *            目标文件路径，如apk/123/123456/com.tencent.qq.apk
	 */
	private static boolean move(String source, String dest) {
		if (isBlank(source) || isBlank(dest)) {
			return false;
		}

		File sourceFile = new File(BASE_DIR + FILE_SPERATOR + source);
		if (!sourceFile.exists()) {
			return false;
		}

		String destDir = dest.substring(0, dest.lastIndexOf(FILE_SPERATOR) + 1);

		checkDirExists(destDir);

		boolean success = sourceFile.renameTo(new File(BASE_DIR + FILE_SPERATOR
				+ dest));
		return success;
	}

	/**
	 * 获取绝对路径
	 * 
	 * @param fileName
	 * @param dir
	 *            不以'/'开头，但以'/'结尾，如apk/123/123456/
	 * @return
	 */
	public static String getAbsolutePath(String fileName, String dir) {
		if (isBlank(fileName) || isBlank(dir)) {
			return null;
		}

		return BASE_DIR + FILE_SPERATOR + dir + fileName;
	}

	/**
	 * 获取绝对路径
	 */
	public static String getAbsolutePath(String relativePath) {
		if (isBlank(relativePath)) {
			return null;
		}

		return BASE_DIR + FILE_SPERATOR + relativePath;
	}

	/**
	 * 激活文件 返回的list的顺序对应于参数list
	 */
	public static List<String> activeFiles(List<String> paths) {
		if (paths == null || paths.size() == 0) {
			return new ArrayList<String>();
		}
		Set<String> set = new HashSet<String>();
		List<String> list = new ArrayList<String>();
		for (String path : paths) {
			if (path == null) {
				list.add(null);
			} else if (set.contains(path)) {
				list.add(null);
			} else {
				list.add(activeFile(path));
			}

			set.add(path);
		}
		return list;
	}

	/**
	 * 获取后缀名
	 * 
	 * @param path
	 * @return
	 */
	public static String getSuffix(String path) {
		if (isBlank(path)) {
			return null;
		}
		return path.substring(path.lastIndexOf(".") + 1, path.length());
	}

	public static boolean isBlank(String str){
		return str==null || str.equals("");
	}
	
	/**
	 * 保存BufferedImage到指定目录
	 * @param bufImg
	 * @param dir 保存目录
	 * @param imgType 图片类型
	 * @return
	 * @throws IOException
	 */
	public static String saveBufferedImage(BufferedImage bufImg,
			String dir,String imgType) throws IOException{
		
		if (isBlank(dir) || isBlank(imgType)) {
			return null;
		}
		
		checkDirExists(dir);
		
		String path = dir + FILE_SPERATOR + System.nanoTime() + "." +imgType;
		String absolutePath = getAbsolutePath(path);
        ImageIO.write(bufImg, imgType, new File(absolutePath));
        return path;
	}
	
	public static Integer[] getImageWidthHeight(File f){
		if(f != null && f.exists()){
			BufferedImage bufferedImage = null;
			try {
				bufferedImage = ImageIO.read(f);
				int width = bufferedImage.getWidth();   
				int height = bufferedImage.getHeight(); 
				Integer [] r = {width,height};
				return r;
			} catch (IOException e) {
				System.err.println("解析图片出错，"+f.getName()+"有可能不是图片文件");
			}   
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		long t = System.currentTimeMillis();
		File file = new File("/Users/duyisong/Downloads/image/485802.jpg");
		String path = saveFileToTempDir(file, "jpg");
		long t1 = System.currentTimeMillis();
		System.out.println(t1 - t);
		System.out.println(path);
	}
	
	

}
