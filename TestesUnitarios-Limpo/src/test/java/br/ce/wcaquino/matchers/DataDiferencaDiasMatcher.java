package br.ce.wcaquino.matchers;

import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {

	private Integer diasAPartirDeHoje;
	
	public DataDiferencaDiasMatcher(Integer diasAPartirDeHoje) {
		this.diasAPartirDeHoje = diasAPartirDeHoje;
	}

	@Override
	public void describeTo(Description description) {
		String formatPattern = "E MMM dd yyyy z HH:mm:ss";
		DateFormat df = new SimpleDateFormat(formatPattern);
		description.appendText(df.format(obterDataComDiferencaDias(diasAPartirDeHoje)));		
	}

	@Override
	protected boolean matchesSafely(Date item) {
		return DataUtils.isMesmaData(item, obterDataComDiferencaDias(diasAPartirDeHoje));
	}

}
