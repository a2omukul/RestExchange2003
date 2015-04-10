import java.util.ArrayList;
import java.util.HashMap;

import com.independentsoft.webdav.exchange.SearchResultRecord;

public class EmailGetResponse {
public String id;
public int offset;
public int fetchCount;
public int toalCount;
public ArrayList<HashMap<String, String>> items;

public EmailGetResponse(String id,int offset,int fetchCount,int totalCount,ArrayList<HashMap<String, String>> items)
{
      this.id= id;
      this.offset = offset;
      this.fetchCount=fetchCount;
      this.toalCount=totalCount;
      this.items=items;
}
}
