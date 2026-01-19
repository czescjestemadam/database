package dev.czescjestemadam.database.utils;

public abstract class Str {
	public static String kebabCase(String str) {
		return convertToLowerCase(str, "-", '_');
	}

	public static String snakeCase(String str) {
		return convertToLowerCase(str, "_", '-');
	}

	public static String plural(String str) {
		final char lastChar = str.charAt(str.length() - 1);
		if (lastChar == 's' || lastChar == 'S') {
			return str;
		}

		return str + (Character.isUpperCase(lastChar) ? 'S' : 's');
	}

	private static String convertToLowerCase(String str, String separator, char otherSeparator) {
		final StringBuilder out = new StringBuilder();

		final char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			final char c = chars[i];

			final char previous = i > 0 ? chars[i - 1] : 0;
			final char next = i + 1 < chars.length ? chars[i + 1] : 0;

			// aA / a0
			if (Character.isLowerCase(previous) && (Character.isUpperCase(c) || Character.isDigit(c))) {
				out.append(separator);
			}

			// AAa
			if (Character.isUpperCase(previous) && Character.isUpperCase(c) && Character.isLowerCase(next)) {
				out.append(separator);
			}

			if (c == otherSeparator) {
				out.append(separator);
			} else {
				out.append(Character.toLowerCase(c));
			}
		}

		return out.toString();
	}
}
