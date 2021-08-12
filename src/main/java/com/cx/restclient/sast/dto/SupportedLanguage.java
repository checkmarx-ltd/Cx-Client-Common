package com.cx.restclient.sast.dto;

import java.util.Locale;

public enum SupportedLanguage {

	ENUS(new Locale("en"),"High","Medium","Low","Information"),
	JAJP(new Locale("ja"),"高","中","低","情報"),
    FRFR(new Locale("fr"),"Haute","Moyenne","Basse","Informations"),
    PTBR(new Locale("pt"),"Alto","Médio","Baixo","Em formação"),
    ESES(new Locale("es", "ES"),"Alta","Medio","Baja","Información"),
    KOKR(new Locale("ko"),"높음","중간","낮음","정보"),
    ZHCN(Locale.SIMPLIFIED_CHINESE,"高危","中危","低危","信息"),
    ZHTW(Locale.TRADITIONAL_CHINESE,"高","中","低","信息"),
    RURU(new Locale("ru"),"Высокое","Среднее","Низкое","Информация");
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
