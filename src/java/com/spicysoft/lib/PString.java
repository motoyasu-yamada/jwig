package com.spicysoft.lib;

public class PString {
	public static enum QuoteStyle {
		COMPAT, NOQUOTES, QUOTES
	}

	/**
	 * 特殊文字を HTML エンティティに変換する
	 *
	 * @param string 変換される文字列。
	 * @param quoteStyle
	 * オプションの 2 番目の引数 quote_style は、
	 * シングルおよびダブルクオートされた文字をどのように扱うかを指定します。
	 * デフォルトの ENT_COMPATは下位互換性のためのモードで、
	 * ダブルクオートは変換しますがシングルクオートは変換しません。
	 * ENT_QUOTESが設定されている場合は、
	 * シングルクオートとダブルクオートを共に変換します。
	 * ENT_NOQUOTESが設定されている場合は、
	 * シングルクオートとダブルクオートは共に変換されません。
	 * @return 変換後の文字列を返します。
	 */
	public static String htmlspecialchars(final String string,
			final QuoteStyle quoteStyle) {
		if (string == null) {
			return "";
		}

		final StringBuffer sb = new StringBuffer();

		for (final char c : string.toCharArray()) {
			if (c == '<') {
				sb.append("&lt;");
			} else if (c == '>') {
				sb.append("&gt;");
			} else if (c == '\'') {
				switch(quoteStyle) {
				case QUOTES:
					sb.append("&#039;");
					break;
				case COMPAT:
				case NOQUOTES:
					sb.append(c);
				}
			} else if (c == '"') {
				switch(quoteStyle) {
				case COMPAT:
				case QUOTES:
					sb.append("&quot;");
					break;
				case NOQUOTES:
					sb.append(c);
					break;
				}

			} else if (c == '&') {
				sb.append("&amp;");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String htmlspecialchars(final String string) {
		return htmlspecialchars(string,QuoteStyle.COMPAT);
	}

}
