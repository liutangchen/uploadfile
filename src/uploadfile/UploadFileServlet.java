package uploadfile;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;

@WebServlet(urlPatterns = { "/uploadServlet" })
public class UploadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doget!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//fileupload.jar
		//1.����һ��������
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//factory�����࣬�������úܶ���ϴ��ļ�����������
		//public DiskFileItemFactory(int sizeThreshold,File repository)
		//sizeThreshold:���������ڴ棬������Դ���ϴ��ļ������������ļ��ŵ��ڴ����
		//              50G�Ĵ��ļ����ڴ�Ų��£��ڴ�������󣬲�������
		//              ���ƣ�sizeThreshold�ٽ�ֵ��600KB  �ϴ����ļ���С��600KB���ѽ��յ��������ļ�
		//              �����ڴ棬�������ֱ�Ӵ��ڴ浱���õ������ļ�
		//              �������ļ�����600KB���Ѵ��������ļ��ֳɺܶಿ�֣����ڴ����ϵ�ĳ����ʱ�ļ���
		//              ������Ҫ�����ļ�����ȥ�ڴ����ʱ�ļ��������ʱ�ļ���ȥȡ
		//repository:������ʱ�ļ��е�
		
		
	}
}
