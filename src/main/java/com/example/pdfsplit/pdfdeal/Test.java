package com.example.pdfsplit.pdfdeal;


/**
 * PDF 切图 测试
 */
public class Test {

    public static void main(String[] args) {
        String inputFullPath = "D:\\360MoveData\\Users\\28442\\Desktop\\1.pdf";
        MulThreadUtil.splitPdf(inputFullPath);
    }

}