package com.spicysoft.lib;

public class PString {
	public static enum QuoteStyle {
		COMPAT, NOQUOTES, QUOTES
	}

	/**
	 * ���ꕶ���� HTML �G���e�B�e�B�ɕϊ�����
	 *
	 * @param string �ϊ�����镶����B
	 * @param quoteStyle
	 * �I�v�V������ 2 �Ԗڂ̈��� quote_style �́A
	 * �V���O������у_�u���N�I�[�g���ꂽ�������ǂ̂悤�Ɉ��������w�肵�܂��B
	 * �f�t�H���g�� ENT_COMPAT�͉��ʌ݊����̂��߂̃��[�h�ŁA
	 * �_�u���N�I�[�g�͕ϊ����܂����V���O���N�I�[�g�͕ϊ����܂���B
	 * ENT_QUOTES���ݒ肳��Ă���ꍇ�́A
	 * �V���O���N�I�[�g�ƃ_�u���N�I�[�g�����ɕϊ����܂��B
	 * ENT_NOQUOTES���ݒ肳��Ă���ꍇ�́A
	 * �V���O���N�I�[�g�ƃ_�u���N�I�[�g�͋��ɕϊ�����܂���B
	 * @return �ϊ���̕������Ԃ��܂��B
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
