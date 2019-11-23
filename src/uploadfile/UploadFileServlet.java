package uploadfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(urlPatterns = { "/uploadServlet" })
public class UploadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doget!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.��ȡ�ʹ��������ļ�������Ŀ¼����ʱĿ¼
		String savePath = req.getSession().getServletContext().getRealPath("/WEB-INF/upload"); //�����ļ��ķ������ϵľ���·��
		String tempPath = req.getSession().getServletContext().getRealPath("/WEB-INF/temp"); //��ʱĿ¼
		File tempFile = new File(tempPath);
		if (!tempFile.exists()) {
			tempFile.mkdirs();   //�����ʱĿ¼�����ڵĻ������ô���ķ�ʽ������һ����Ŀ¼
		}
		
		//2.����һ��DiskFileItemFactory����
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(100*1024); //��λ���ֽ� , 100KB,�ϴ����ļ�<100KB,�����ڴ��У�>100KB,��tempPath
		factory.setRepository(tempFile); //�����ϴ��ļ�����ʱĿ¼
		
		//3.����һ���ļ��ϴ�������
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory); //�õ��ļ��Ľ�����
		servletFileUpload.setFileSizeMax(20*1024*1024); //�����ϴ������ļ��Ĵ�С��20M����
		servletFileUpload.setHeaderEncoding("UTF-8"); //��ֹ�ϴ���������Ϣ������
		servletFileUpload.setFileSizeMax(40*1024*1024); //���ƶ���ļ�ͬʱ�ϴ���ʱ���ܴ�С���ֵ40M
		servletFileUpload.setProgressListener(new ProgressListener() {
			@Override
			public void update(long yUploadFileSize, long uploadFileSize, int item) {
				System.out.println("�ϴ��ļ��ܴ�СΪ��" + uploadFileSize + ",�Ѿ��ϴ��ļ��Ĵ�С��" + yUploadFileSize);
			}
		});
		
		//4.�ж��ύ�����������Ƿ����ϴ��������ݣ��ǲ���Multipart���뷽ʽ   ServletFileUpload.isMultipartContent(request)
		if (!ServletFileUpload.isMultipartContent(req)) {
			return;
		}
		
		//5.ʹ��ServletFileUpload�����������ϴ����ݣ�����������ص���һ��List<FileItem>����
		OutputStream out = null;
		InputStream in = null;
		try {
			List<FileItem> fileList = servletFileUpload.parseRequest(req);
			if (fileList != null && fileList.size() > 0) {
				for(FileItem fileItem:fileList) {
					if (fileItem.isFormField()) {
						//����ͨ����ļ�ֵ����ʾ����
						System.out.println("��ͨ�ı��nameΪ��" + fileItem.getFieldName());
						System.out.println("��ͨ�ı��valueΪ��" + fileItem.getString("UTF-8"));
					}else {
						//���ļ���ͨ��fileItem,�õ��ϴ��������ļ��ĸ�����Ϣ�����ļ����ı�
						String fileName = fileItem.getName(); //�õ��ļ������� xxx.doc  xxx.txt
						if (fileName == null || fileName.trim().equals("")) {
							continue;
						}
						//ע�����IE�õ���fileName�Ǵ��о���·����D:\abc\xxx.doc  ;   ���������õ���  xxx.doc
						fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
						String fileType = fileItem.getContentType(); //�õ��ļ�������image/jpg
						long fileSize = fileItem.getSize(); //�õ��ļ����ܴ�С
						//�õ��ļ���׺��
						String fileEx = fileName.substring(fileName.lastIndexOf(".")+1);
						//��֤��׺�ĺϷ���
						if (fileEx.equals("rar") || fileEx.equals("zip")) {
							throw new RuntimeException("��ֹ�ϴ�ѹ���ļ���");
						}
						//���ļ���д�뱣���Ŀ¼��(�����µ��ļ���������һ��Ŀ¼���ļ�̫��������µĴ洢Ŀ¼)
						String saveFileName = makeFileName(fileName);
						String realSavePath = makePath(saveFileName,savePath);
						//�ȴ���һ�������
						out = new FileOutputStream(realSavePath + "\\" + saveFileName);
						in = fileItem.getInputStream();
						//��������������һ�������ļ�������������
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len=in.read(buffer)) > 0){
							out.write(buffer, 0, len);
						}
						in.close();
						out.close();
					}
				}
			}
		} catch (FileUploadBase.FileSizeLimitExceededException e) {
			System.out.println("�����ļ���С�������ƣ�");
		}catch (FileUploadBase.SizeLimitExceededException e) {
			System.out.println("���ļ���С�������ƣ�");
		}catch (Exception e) {
			System.out.println("�ϴ��ļ�ʧ�ܣ�");
		}finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	private String makePath(String saveFileName, String savePath) {
		//�õ��ļ����֣����ڴ浱�е�ַ��hashCodeֵ
		int hashCode = saveFileName.hashCode();
		int dir1 = hashCode&0xf; //dir1��ֵ���������������Χ0-15
		int dir2 = (hashCode >> 4)&0xf; //�õ��Ľ������0-15��Χ��
		//��dir1,dir2�����ҵ��µĴ洢�ļ���Ŀ¼
		String dir = savePath + "\\" + dir1 + "\\" + dir2;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}

	private String makeFileName(String fileName) {
		//uuid
		return UUID.randomUUID().toString()+"_"+fileName;
	}
}
