package demo.qr.qrCode;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 
 * @author Administrator
 *
 * 二维码工具类
 *
 */
public class QRCodeUtil {

	private static final String CHAR_SET="utf-8";
	
	private static final String FORMAT="JPG";
	
	private static final int QRCODE_SIZE=300;
	
	private static final int LOGO_WIDTH=60;
	
	private static final int LOGO_HEIGHT=60;
	
	/**
	 * 创建二维码图片缓存流
	 * @param content 
	 * 内容
	 * @param logoPath 
	 * LOGO地址
	 * @param needCompress
	 * 是否压缩
	 * @return
	 * 二维码图片缓存流
	 * @throws Exception
	 */
	private static BufferedImage createImage(String content,String logoPath,boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints=new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHAR_SET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix=new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,hints);
		int width=bitMatrix.getWidth();
		int height=bitMatrix.getHeight();
		BufferedImage image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y)?0xFF000000:0xFFFFFFFF);
			}
		}
		if (logoPath!=null&&!logoPath.equals("")) {
			QRCodeUtil.insertImage(image, logoPath, true);
		}
		return image;
	}
	
	/**
	 * 插入LOGO
	 * @param source 
	 * 二维码缓存流
	 * @param logoPath 
	 * LOGO图片地址
	 * @param needCompress 
	 * 是否压缩
	 * @throws Exception
	 */
	private static void insertImage(BufferedImage source,String logoPath,boolean needCompress) throws Exception {
		File file=new File(logoPath);
		if (!file.exists()) {
			throw new Exception("logo file not found");
		}
		Image src=ImageIO.read(new File(logoPath));
		int width=src.getWidth(null);
		int height=src.getHeight(null);
		if (needCompress) {
			if (width>LOGO_WIDTH) {
				width=LOGO_WIDTH;
			}
			if (height>LOGO_HEIGHT) {
				height=LOGO_HEIGHT;
			}
			Image image=src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g=tag.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			src=image;
		}
		Graphics2D g2d=source.createGraphics();
		int x=(QRCODE_SIZE-width)/2;
		int y=(QRCODE_SIZE-height)/2;
		g2d.drawImage(src, x, y, width,height,null);
		Shape shape=new RoundRectangle2D.Float(x, y, width, height, 6, 6);
		g2d.setStroke(new BasicStroke(3f));
		g2d.draw(shape);
		g2d.dispose();
		
	}
	/**
	 * 生成二维码(内嵌LOGO)
	 * @param content 
	 * 内容
	 * @param logoPath 
	 * LOGO地址
	 * @param destPath 
	 * 存放目录
	 * @param needCompress 
	 * 压缩
	 * @return 
	 * 二维码文件名
	 * @throws Exception
	 */
	public static String encode(String content,String logoPath,String destPath,boolean needCompress) throws Exception {
		BufferedImage image=QRCodeUtil.createImage(content, logoPath, needCompress);
		mkdirs(destPath);
		String fileName=new Date().getTime()+"."+FORMAT.toLowerCase();
		ImageIO.write(image, FORMAT, new File(destPath+"/"+fileName));
		return fileName;
	}
	
	/**
	 * 生成二维码
	 * @param content
	 * 内容
	 * @param destPath
	 * 存放目录
	 * @return
	 * 二维码文件名
	 * @throws Exception
	 */
	public static String encode(String content,String destPath) throws Exception {
		return QRCodeUtil.encode(content, null, destPath, true);
	}
	
	/**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．
     * (mkdir如果父目录不存在则会抛出异常)
     * @param destPath
     * 存放目录
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }
    
    /**
     * 解析二维码
     * @param file
     * 二维码图片
     * @return
     * 内容
     * @throws Exception 
     */
    public static String decode(File file) throws Exception {
		BufferedImage image;
		image=ImageIO.read(file);
		if (image==null) {
			return null;
		}
		BufferedImageLuminanceSource source=new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap=new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		Hashtable<DecodeHintType, Object> hints=new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, CHAR_SET);
		result=new MultiFormatReader().decode(bitmap,hints);
		String resultStr=result.getText();
		return resultStr;
	}
    
    /**
     * 解析二维码
     * @param file
     * 二维码图片
     * @return
     * 内容
     * @throws Exception 
     */
    public static String decode(String path) throws Exception {
		return QRCodeUtil.decode(new File(path));
	}
    
	
}
