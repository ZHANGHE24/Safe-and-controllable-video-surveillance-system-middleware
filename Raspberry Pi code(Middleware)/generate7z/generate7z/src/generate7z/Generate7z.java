package generate7z;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Scanner;

import encryption.SM2;
import encryption.SM3;
import encryption.SM4;


class VideoThread extends Thread {
//	private static final String OUTPUT_DIRECTORY = "C:\\Users\\zh200\\Desktop\\";
//    private static final String TAR_GZIP_SUFFIX = ".7z";
//
//    private static final String MULTIPLE_RESOURCES = "1";
//
//    private static final String MULTIPLE_RESOURCES_PATH = OUTPUT_DIRECTORY + MULTIPLE_RESOURCES + TAR_GZIP_SUFFIX;
	private String PY_URL;
	private String file;
	private String path;
	private String temp;
	VideoThread(String PY_URL){
		this.PY_URL = PY_URL;
	}
	public void run() {
		
		//����python����
		Generate7z g7z = new Generate7z();
		try {
			temp = g7z.execPy(PY_URL);
			System.out.println(temp); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		System.out.print("��ʼת��");
//		g7z.convert_to_mp4(temp+".h264", temp+".mp4");
//		System.out.print("ת�����");
		System.out.println("java��ʼ");
		
		
//		file = temp+".mp4";
		file = "test.mp4";
//		path = "/home/zhxf/Desktop/projectcode/Resource/"+temp;
		path = "C:\\Users\\zh200\\Desktop\\��ݮ�ɴ���\\generate7z\\generate7z\\Resource\\"+temp+"\\";
		File createfile = new File(path);  
		if(!createfile.exists()){  
			createfile.mkdirs();  
		} 
		try {
			g7z.func(file, path);//file���Ƿ��ز�����path�Լ���
			String cmds = "7z a -t7z -r "+temp+".7z "+path +"*";
			try{
				Process ps = Runtime.getRuntime().exec(cmds);   
				System.out.print(g7z.loadStream(ps.getInputStream()));
				System.err.print(g7z.loadStream(ps.getErrorStream()));
				}catch(IOException e){
					e.printStackTrace();   
				}   
				
//			Class clazz = VideoThread.class;
//			File resource1 = new File(clazz.getResource(path+"Reading summary - IELTS-Simon-s video course"+".enc").getFile());
//			File resource2 = new File(clazz.getResource(path+"encryption.txt").getFile());
//			SevenZ.compress(MULTIPLE_RESOURCES_PATH, resource1, resource2);
        } catch (Exception e) {
			e.printStackTrace();
		}
		File delfile = new File(path);
		if(g7z.deleteFile(delfile)) {
			System.out.print("ɾ���ɹ�");
		}
		System.out.println("java���");
		
	}
}

public class Generate7z {
	/**
	 * �ȸ�������ݹ�ɾ���ļ���
	 *
	 * @param dirFile Ҫ��ɾ�����ļ�����Ŀ¼
	 * @return ɾ���ɹ�����true, ���򷵻�false
	 */
	public boolean deleteFile(File dirFile) {
	    // ���dir��Ӧ���ļ������ڣ����˳�
	    if (!dirFile.exists()) {
	        return false;
	    }

	    if (dirFile.isFile()) {
	        return dirFile.delete();
	    } else {

	        for (File file : dirFile.listFiles()) {
	            deleteFile(file);
	        }
	    }

	    return dirFile.delete();
	}
	public void convert_to_mp4(String source_path, String target_path) {
		
		try {
			String cmod="MP4Box -add "+source_path+" -new "+target_path;
	    	Process p = Runtime.getRuntime().exec(cmod);
	     	p.waitFor();
	    	p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static String getstr(File file){
        String str = "";
        try{
            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            StringBuilder b = new StringBuilder();
            String s = "";
            while((s =bReader.readLine()) != null){
                b.append(s);
            }
            bReader.close();
            str = b.toString();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
	
	public String loadStream(InputStream in)throws IOException{   
		  int ptr = 0;
		  in = new BufferedInputStream(in);   
		  StringBuffer buffer = new StringBuffer();
		  while((ptr = in.read()) != -1){
		  buffer.append((char)ptr);
		  }
		  return buffer.toString();
		  }   
	public String execPy(String PY_URL) throws InterruptedException {
		Process proc = null;
		String result = "";
		try {
			proc = Runtime.getRuntime().exec("python " + PY_URL);
			//proc.waitFor();
			InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            result = input.readLine();
            input.close();
            ir.close();
            System.out.println("pyִ�����");
          //proc.waitFor();
            
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public String func(String file, String path) throws Exception {
		SM2 sm2 = new SM2();
		SM4 sm4 = new SM4();
		SM3 sm3 = new SM3();
		String key = sm4.func_cryptsm4(file,path);
		String sp1 = sm2.func_cryptsm2(key);
		FileWriter writer1;
        try {
            writer1 = new FileWriter("SM4KEY");
            writer1.write(key);
            writer1.flush();
            writer1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		System.out.println("SM4�Գ���Կ:"+key);
		File f1 = new File("PubKey");
        String pubk = getstr(f1);
		String sp2 = sm3.func_cryptsm3(pubk);
		System.out.println("SM2����SM4��Կ:"+sp1);
		System.out.println("SM3ǩ��SM2��Կ���:"+sp2);
        FileWriter writer2;
        try {
            writer2 = new FileWriter(path+"encryption");
            writer2.write(sp1+sp2);
            writer2.flush();
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return sp1;
	}
	public static void main(String args[]) {
		String filepath = "test.py";
		while(true) {
			VideoThread T = new VideoThread(filepath);
			T.start();
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}