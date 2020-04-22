package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprio {

		public static DiaSemanaMatcher caiEm(Integer diaSemana) {
			return new DiaSemanaMatcher(diaSemana);
		}
		
		public static DiaSemanaMatcher caiNumaSegunda() {
			return new DiaSemanaMatcher(Calendar.MONDAY);
		}
		
		public static DataDiferencaDiasMatcher ehHoje() {
			return new DataDiferencaDiasMatcher(0);
		}
		
		public static DataDiferencaDiasMatcher ehHojeComDiferencaDias(Integer dias) {
			return new DataDiferencaDiasMatcher(dias);
		}
}
