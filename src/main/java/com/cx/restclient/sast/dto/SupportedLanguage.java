package com.cx.restclient.sast.dto;

import java.util.Locale;

public enum SupportedLanguage {

	en_US(new Locale("en"),"High","Medium","Low","Information"),
	ja_JP(new Locale("ja"),"高","中","低","情報"),
    fr_FR(new Locale("fr"),"Haute","Moyenne","Basse","Informations"),
    pt_BR(new Locale("pt"),"Alto","Médio","Baixo","Em formação"),
    es_ES(new Locale("es", "ES"),"Alta","Medio","Baja","Información"),
    ko_KR(new Locale("ko"),"높음","중간","낮음","정보"),
    zh_CN(Locale.SIMPLIFIED_CHINESE,"高危","中危","低危","信息"),
    zh_TW(Locale.TRADITIONAL_CHINESE,"高","中","低","信息"),
    ru_RU(new Locale("ru"),"Высокое","Среднее","Низкое","Информация");

    //TODO: Add fitting format
//    JAPANESE(new Locale("ja-JP"), "ss"),
//    KOREAN(new Locale("ko-KR"), "ss"),
//    PORTUGUESE_BR(new Locale("pt-BR"), "ss"),
//    CHINESE_CN(new Locale("zn-CN"), "ss"),
//    CHINESE_TW(new Locale("zn-TW"), "ss");

    private final Locale locale;
    private final String High;
    private final String Medium;
	private final String Low;
    private final String Information;
    
    private SupportedLanguage(Locale locale, String high, String medium, String low, String information) {
		this.locale = locale;
		High = high;
		Medium = medium;
		Low = low;
		Information = information;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getHigh() {
		return High;
	}

	public String getMedium() {
		return Medium;
	}

	public String getLow() {
		return Low;
	}

	public String getInformation() {
		return Information;
	}
    

   
}
