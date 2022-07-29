package com.example.pdfsplit.pdfdeal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
/*执行时间统计*/
public class TimeStatic {
    public static List<String> TIME_RECORD = new LinkedList<>();
    public final static String TRAIL = ".txt";

    public static void recordTime(ProcessNodeEnum processNode) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("mm分ss秒SSS");
        String nowTime = df.format(LocalDateTime.now());
        TIME_RECORD.add(processNode.getName() + "\t" + nowTime);
    }


    public enum ProcessNodeEnum {//todo 1.支持用户参数 2.开始作为参数传给结束 3.
        TASK_START("任务开始"), TASK_END("任务结束"),
        ImgDeal_START("图片任务列表开始"), ImgDeal_END("图片任务列表结束"),
        Page_START("页处理开始"), Page_END("页处理结束"),
        //        _START("开始"), _END("结束"),
//    PRISE_START("解析处理开始"), PRISE_END("解析处理结束"),
        Page_Render("图片转换节点"), TEST2("测试节点2"),
        SPLIT_START("分页开始"), SPLIT_END("分页结束"),
        SAVE_START("保存开始"), SAVE_END("保存结束");

        private final String name;

        ProcessNodeEnum(String name) {
            this.name = name;

        }

        public String getName() {
            return name;
        }//
    }
}
