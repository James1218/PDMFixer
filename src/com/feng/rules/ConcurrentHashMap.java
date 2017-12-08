package com.feng.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/*
     HashMap map1 = new HashMap();
     Map map1 = new HashMap(); 	// fine for single-threaded access
    Map map2 = new ConcurrentHashMap();  // preferred for use with multiple threads
    
    import java.util.concurrent.ConcurrentHashMap;
 */

public class ConcurrentHashMap {

	private static int count = 0;

	public static void main(String[] args) {
		
//		String [] directoryList = {"C:\\Workspace_Feng_Yang\\NewWorkSpace2\\JavaTraining\\TestFile\\CodeMerge_08_2018\\ConcurrentHashMap"};
		String [] directoryList = {"C:\\Workspace_Feng_Yang\\UI_1.2\\Benefits", 
									"C:\\Workspace_Feng_Yang\\UI_1.2\\BenefitsWY",
									"C:\\Workspace_Feng_Yang\\UI_1.2\\Framework",
									"C:\\Workspace_Feng_Yang\\UI_1.2\\Tax",
									"C:\\Workspace_Feng_Yang\\UI_1.2\\TaxWY",
									"C:\\Workspace_Feng_Yang\\UI_1.2\\UIWorkflowListener"};
		
		
		for (String directoryPath : directoryList){
			List<File> list = listAllFiles(directoryPath);
			Iterator<File> iterator = list.iterator();
			while (iterator.hasNext()){
				helper(iterator.next().getAbsolutePath());
			}
		}
		System.out.println("Done");
		System.out.println(count);
	}
	
	private static void helper(String filePath){
		if (!filePath.endsWith("java")) return;
		try (FileReader fileReader = new FileReader(filePath);
				BufferedReader bf = new BufferedReader(fileReader)){
			
			int countLocal = 0;
			String input = bf.lines().collect(Collectors.joining("\n"));
			StringBuffer sb = new StringBuffer();
			
			
			String regex = "(\\b)(Hash)?Map(.*?\\=\\s*?)(new\\s+HashMap)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			boolean needOverWrite = false;
			while(matcher.find()){
//				System.out.println("***************************");
//				System.out.println(matcher.group());
//				System.out.println(matcher.group(1));
//				System.out.println(matcher.group(2));
//				System.out.println(matcher.group(3));
				matcher.appendReplacement(sb, "");
				sb.append(matcher.group(1));
				sb.append("ConcurrentHashMap");
				sb.append(matcher.group(3));
				sb.append("new ConcurrentHashMap");
				needOverWrite = true;
				count++;
				countLocal++;
			}
			matcher.appendTail(sb);
			input = sb.toString();
			
			if (needOverWrite){
				String importStatement = "import java.util.concurrent.ConcurrentHashMap;";
				if (!input.contains(importStatement)){
					input = addImport(input, importStatement);
				}
//				System.out.println(input);
				try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)){
					fileOutputStream.write(input.getBytes());
					fileOutputStream.close();
				}
				System.out.println(filePath);
				System.out.println(countLocal);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String addImport(String input, String importStatement){
		StringBuffer sb = new StringBuffer();
		String regex_package = "package.*?;";
		Pattern pattern = Pattern.compile(regex_package);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()){
			matcher.appendReplacement(sb, "");
			sb.append(matcher.group());
			sb.append("\n\n" + importStatement + "\n");
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	private static List<File> listAllFiles (String directoryPath){
		List<File> list = new LinkedList<>();
		File directory = new File(directoryPath);
		File [] fileList = directory.listFiles();
		for (File file : fileList){
			if (file.isFile()){
				list.add(file);
			}
			else if (file.isDirectory()){
				list.addAll(listAllFiles(file.getAbsolutePath()));
			}
		}
		return list;
	}

}
