package com.cx.restclient.sast.dto;

import java.util.Locale;

public enum SupportedLanguage {

	enUS(new Locale("en"),"High","Medium","Low","Information"),
	jaJP(new Locale("ja"),"高","中","低","情報"),
    frFR(new Locale("fr"),"Haute","Moyenne","Basse","Informations"),
    ptBR(new Locale("pt"),"Alto","Médio","Baixo","Em formação"),
    esES(new Locale("es", "ES"),"Alta","Medio","Baja","Información"),
    koKR(new Locale("ko"),"높음","중간","낮음","정보"),
    zhCN(Locale.SIMPLIFIED_CHINESE,"高危","中危","低危","信息"),
    zhTW(Locale.TRADITIONAL_CHINESE,"高","中","低","信息"),
    ruRU(new Locale("ru"),"Высокое","Среднее","Низкое","Информация");

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
