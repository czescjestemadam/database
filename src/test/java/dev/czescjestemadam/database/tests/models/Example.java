package dev.czescjestemadam.database.tests.models;

import dev.czescjestemadam.database.model.Model;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Example extends Model<Example> {
	public Integer id;
	public String str;
	@Nullable
	public String strNullable;
	public String strDflt;
	public String strUnique;

	public Example() {
	}

	public Example(Integer id, String str, @Nullable String strNullable, String strDflt, String strUnique) {
		this.id = id;
		this.str = str;
		this.strNullable = strNullable;
		this.strDflt = strDflt;
		this.strUnique = strUnique;
	}

	@Override
	public Example copy() {
		return new Example(id, str, strNullable, strDflt, strUnique);
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public final boolean equals(Object o) {
		return o instanceof final Example example &&
				id.equals(example.id) &&
				str.equals(example.str) &&
				Objects.equals(strNullable, example.strNullable) &&
				strDflt.equals(example.strDflt) &&
				strUnique.equals(example.strUnique);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + str.hashCode();
		result = 31 * result + Objects.hashCode(strNullable);
		result = 31 * result + strDflt.hashCode();
		result = 31 * result + strUnique.hashCode();
		return result;
	}
}
