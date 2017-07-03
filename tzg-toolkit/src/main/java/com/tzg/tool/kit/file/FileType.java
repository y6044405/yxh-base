package com.tzg.tool.kit.file;

/**
 * 常见文件类型.
 */
public enum FileType {
    // jpg ffd8ffe000104a464946、jpeg 
    JPG("FFD8FF"), JPEG("FFD8FF"),
    // PNG.89504e470d0a1a0a0000
    PNG("89504E470D0A1A0A0000"),
    // gif 47494638396126026f01
    GIF("47494638396126026F01"),
    // tiff 49492A00227105008037
    TIFF("49492A00"),
    // Windows Bitmap 16位色：424d228c010000000000;24位：424d8240090000000000；256位：424d8e1b030000000000
    BMP("424D"),
    // cad、dwg 41433130313500000000
    DWG("41433130"),
    // Adobe Photoshop 38425053000100000000
    PSD("38425053"), PS("252150532D41646F6265"),
    // Rich Text Format.7b5c727466315c616e73
    RTF("7B5C727466"),
    // XML.3c3f786d6c2076657273
    XML("3C3F786D6C"),
    // HTML 3c21444f435459504520 htm 3c21646f637479706520
    HTML("68746D6C3E"), CSS("48544D4C207B0D0A0942"), JS("696B2E71623D696B2E71"),
    // Email 46726f6d3a203d3f6762
    EMAIL("44656C69766572792D646174653A"),
    // Outlook Express
    DBX("CFAD12FEC5FD746F"),
    // Outlook (pst)
    PST("2142444E"),
    // MS Word/Excel d0cf11e0a1b11ae10000
    XLS_DOC("D0CF11E0"),
    //Visio 绘图    
    VSD("D0CF11E0A1B11AE10000"),
    //WPS文字wps、表格et、演示dps都是一样的
    WPS("D0CF11E0A1B11AE10000"), 
    // MS Access 
    MDB("5374616E64617264204A"),
    // WordPerfect (wpd)  
    WPD("FF575043"),
    // Postscript
    EPS("252150532D41646F6265"),
    // Adobe Acrobat 255044462d312e350d0a
    PDF("255044462D312E"),
    // Quicken (qdf)    
    QDF("AC9EBD8F"),
    // Windows Password(pwl)
    PWL("E3828596"),
    // ZIP Archive 504b0304140000000800
    ZIP("504B0304"),
    // RAR Archive 526172211a0700cf9073
    RAR("52617221"),
    // Wave 52494646e27807005741
    WAV("57415645"),
    //audio
    AAC("FFF1508003FFFCDA004C"), WV("7776706BA22100000704"), FLAC("664C6143800000221200"),
    // AVI 52494646d07d60074156 52494646b440c02b4156
    AVI("41564920"),
    // Real Audio(ram)
    RAM("2E7261FD"),
    // Real Media rmvb 
    RM("2E524D46"), RMVB("2E524D46000000120001"),
    //flv与f4v相同    
    FLV("464C5601050000000900"), MP3("49443303000000002176"),
    //video
    MP4("00000020667479706D70"), MKV("1A45DFA3010000000000"),
    // MPEG (mpg) 000001ba210001000180
    MPG("000001BA"),
    // Quicktime(mov)   
    MOV("00000014667479707174"),
    // Windows Media
    ASF("3026B2758E66CF11"), WMV("3026B2758E66CF11A6D9"),
    //3GP
    TGP("00000014667479703367"), INI("235468697320636F6E66"),
    //windows file
    JAR("504B03040A0000000000"), BAT("406563686F206F66660D"), 
    EXE("4D5A9000030000000400"), DLL("4D5A9000"), 
    MF("4D616E69666573742D56"), SQL("494E5345525420494E54"),
    //java
    JAVA("7061636B616765207765"), 
    JSP("3C25402070616765206C"),
    GZ("1F8B0800000000000000"), PROPERTIES("6C6F67346A2E726F6F74"), CLASS("CAFEBABE0000002E0041"), 
    CHM("49545346030000006000"), 
    //mxp
    MXP("04000000010000001300"), DOCX("504B0304140006000800"), 
  
    //torrent
    TORRENT("6431303A637265617465"), TXT("75736167"),
    // MIDI 4d546864000000060001
    MID("4D546864");
  private String value = "";

  private FileType(String value) {
    this.value = value;
  }

    /**
     * 获取图片文件格式.
     * @author:  heyiwu 
     * @return 图片文件类型数组
     */
    public static FileType[] getPicFileType() {
        return new FileType[] { JPG, JPEG, PNG, GIF, BMP, PNG };
    }

    /**
     * 获取视频文件格式
     * @author:  heyiwu 
     * @return
     */
    public static FileType[] getVideoFileType() {
        return new FileType[] { RAM, RM, MOV, RMVB, AVI, FLV, MP4, WMV, TGP, MKV };
    }

    /**
     * 获取音频文件格式
     * @author:  heyiwu 
     * @return
     */
    public static FileType[] getAudioFileType() {
        return new FileType[] { WAV, MID, MP3, AAC, WV, FLAC };
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}