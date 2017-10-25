package demo.qr.qrCode;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
//    	System.out.println(QRCodeUtil.encode(""+new Date().getTime(), "C:\\springMVC.jpg", "C:\\", false));
//    	System.err.println(QRCodeUtil.encode("亲爱哒爱你哟笔芯晚安呐~", "c:\\"));
    	System.err.println(QRCodeUtil.decode("C:\\1508859322570.jpg"));
    }
}
