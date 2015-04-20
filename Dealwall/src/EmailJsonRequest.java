import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.CircularRedirectException;

public class EmailJsonRequest {

	public static class Criteria {
		private String operator, field, query;

		public String getOperator() {
			return operator;
		}

		public String getField() {
			return field;
		}

		public String getQuery() {
			return query;
		}

		public void setOperator(String s) {
			operator = s;
		}

		public void setField(String s) {
			field = s;
		}

		@Override
		public String toString() {
			return operator + "," + field + "," + query;
		}
	}

	private String sortDirection;
	private int fieldCount;

	public String getSortDirection() {
		return sortDirection;
	}

	public String getFieldCount() {
		return sortDirection;
	}

}
