package com.pdfdeal;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.jfif.JfifDirectory;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.pdfdeal.MulThreadUtil.listMulThreadDealOld;


public class ImageUtils {
    static String sep = File.separator;

    public static int getImageDpi(String path) {

        ImageInfo imageInfo = null;
        try {
            imageInfo = Imaging.getImageInfo(new File(path));
        } catch (ImageReadException | IOException e) {
            e.printStackTrace();
        }
        assert imageInfo != null;
        return imageInfo.getPhysicalHeightDpi();

    }

    /**
     * 通过metadata-extractor获取图片数据
     *
     * @param path
     */
    public static void getImageInfoByMetadata(String path) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(path));

            //System.out.println("-----------------start-通过metadata-extractor获取图片数据----------------");
            for (Directory d : metadata.getDirectories()) {
                System.out.println(d.getTags());
            }
            //System.out.println("-----------------end-通过metadata-extractor获取图片数据----------------");

            Iterable<Directory> directoryList = metadata.getDirectories();
            // 图片类型信息
            FileTypeDirectory fileType = null;
            // 图像分辨率信息
            JfifDirectory jfif = null;
            ExifDirectoryBase exif = null;
            for (Directory directory : directoryList) {
                if (directory instanceof FileTypeDirectory) {
                    fileType = (FileTypeDirectory) directory;
                }
                if (directory instanceof JfifDirectory) {
                    jfif = (JfifDirectory) directory;
                }
                if (directory instanceof ExifDirectoryBase) {
                    exif = (ExifDirectoryBase) directory;
                }
            }

            if (fileType == null) {
                throw new Exception("无法识别图片格式");
            }

            int xDpi = -1;
            int yDpi = -1;
            //dpi信息优先从jfif中取，如果没有再从exif中去，都没有就没办法了
            if (jfif != null) {
                xDpi = jfif.getResX();
                yDpi = jfif.getResY();
            }
            if (xDpi <= 0 || yDpi <= 0) {
                if (exif != null) {
                    xDpi = Integer.parseInt(exif.getString(ExifDirectoryBase.TAG_X_RESOLUTION));
                    yDpi = Integer.parseInt(exif.getString(ExifDirectoryBase.TAG_Y_RESOLUTION));
                }
            }

            System.out.println("xDpi:" + xDpi + ",yDpi:" + yDpi);
            if (xDpi <= 0 || yDpi <= 0) {
                throw new Exception("这张图片难搞啊");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pdf2image(String fileAddress, String filename, int indexOfStart, int indexOfEnd, String type, String targetAddress) {
        // 将pdf装图片 并且自定义图片得格式大小

        PDDocument doc = null;
        InputStream is = null;
        try {
            is = new FileInputStream(fileAddress);
            //利用PdfBox生成图像
            doc = Loader.loadPDF(is);
            PDFRenderer renderer = new PDFRenderer(doc);
            int actSize = doc.getNumberOfPages();
            if (indexOfEnd == -1) {
                indexOfEnd = actSize;
            }
            for (int i = indexOfStart; i < indexOfEnd; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 105); // Windows native DPI

                ImageIO.write(image, type, new File(targetAddress + sep + filename + "_" + i + "." + type));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                doc.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void pdf2image(String fileAddress, String filename, String targetAddress) {

        int indexOfStart = 0;
        int indexOfEnd = -1;
        String type = "jpg";

        // 将pdf装图片 并且自定义图片得格式大小

        PDDocument doc = null;
        InputStream is = null;
        try {
            is = new FileInputStream(fileAddress);
            //利用PdfBox生成图像
            doc = Loader.loadPDF(is);
            PDFRenderer renderer = new PDFRenderer(doc);
            int actSize = doc.getNumberOfPages();
            if (indexOfEnd == -1) {
                indexOfEnd = actSize;
            }

            PDDocument finalDoc = doc;
            listMulThreadDealOld(indexOfStart, indexOfEnd, (idx) -> {
                try {
                    FileInputStream is2 = new FileInputStream(fileAddress);
//                    //利用PdfBox生成图像
                    PDDocument doc1 = Loader.loadPDF(is2);
                    PDFRenderer renderer1 = new PDFRenderer(doc1);
                    BufferedImage image = renderer1.renderImageWithDPI(idx, 105); // Windows native DPI
                    ImageIO.write(image, type, new File(targetAddress + sep + filename + "_" + idx + "." + type));
//                    Thread.sleep(1500);
                } catch (Exception e) {
                    System.out.println("报错了");
                    e.printStackTrace();
                    return false;
                }
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                doc.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void pdf2image2() {

    }

/*    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\69052\\Desktop\\测试角度.pdf";

        long l = System.currentTimeMillis();
         ImageUtils.pdf2image(filePath, "任光新卷宗",  "C:\\Users\\69052\\Desktop\\新建");
        long l2 = System.currentTimeMillis();
        System.out.println(l2 - l);

    }*/

}
