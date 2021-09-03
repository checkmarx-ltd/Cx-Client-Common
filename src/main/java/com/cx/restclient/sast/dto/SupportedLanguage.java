package com.cx.restclient.sast.dto;

import java.util.Locale;

public enum SupportedLanguage {

    en_US(new Locale("en-US"),"High","Medium","Low","Information", "EEEE, MMMM dd, yyyy hh:mm:ss a"),
	ja_JP(new Locale("ja-JP"),"高","中","低","情報","yyyy年M月d日 H:mm:ss"),
    fr_FR(new Locale("fr-FR"),"Haute","Moyenne","Basse","Informations","EEEE dd MMMM yyyy HH:mm:ss"),
    pt_BR(new Locale("pt-BR"),"Alto","Médio","Baixo","Em formação", "EEEE, d 'de' MMMM 'de' yyyy HH:mm:ss"),
    es_ES(new Locale("es-ES"),"Altas","Medias","Bajas","Información","EEEE, d 'de' MMMM 'de' yyyy HH:mm:ss"),
    ko_KR(new Locale("ko-KR"),"높음","중간","낮음","정보", "yyyy년 M월 d일 EEEE a h:mm:ss"),
    zh_CN(new Locale("zh-CN"),"高危","中危","低危","信息", "yyyy年M月d日 HH:mm:ss"),
    zh_TW(new Locale("zh-TW"),"高","中","低","信息", "yyyy年M月d日 a hh:mm:ss"),
    ru_RU(new Locale("ru-RU"),"Высокое","Среднее","Низкое","Информация","d MMMM yyyy 'г'. H:mm:ss");
	
    private final Locale locale;
    private final String High;
    private final String Medium;
	private final String Low;
    private final String Information;
    private final String datePattern;
    
    private SupportedLanguage(Locale locale, String high, String medium, String low, String information, String datePattern) {
		this.locale = locale;
		this.High = high;
		this.Medium = medium;
		this.Low = low;
		this.Information = information;
		this.datePattern = datePattern;
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
    
	public String getDatePattern() {
		return datePattern;
	}
   
}
