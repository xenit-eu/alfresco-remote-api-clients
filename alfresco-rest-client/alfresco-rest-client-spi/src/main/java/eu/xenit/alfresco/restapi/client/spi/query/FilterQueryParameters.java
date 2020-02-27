package eu.xenit.alfresco.restapi.client.spi.query;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FilterQueryParameters implements QueryParameters {

    private List<String> whereClauses = new ArrayList<>();

    public FilterQueryParameters whereIsFolder(boolean isFolder) {
        whereClauses.add("isFolder=" + isFolder);
        return this;
    }

    public FilterQueryParameters whereIsFile(boolean isFile) {
        whereClauses.add("isFile=" + isFile);
        return this;
    }

    public FilterQueryParameters whereNodeType(String nodeType) {
        whereClauses.add("nodeType='" + nodeType + "'");
        return this;
    }

    public FilterQueryParameters whereAssocType(String assocType) {
        whereClauses.add("assocType='" + assocType + "'");
        return this;
    }

    public FilterQueryParameters whereIsPrimary(boolean isPrimary) {
        whereClauses.add("isPrimary=" + isPrimary);
        return this;
    }

    public FilterQueryParameters where(String whereClause) {
        whereClauses.add(whereClause);
        return this;
    }

    @Override
    public Params queryParameters() {
        return new Params().putIfNonEmpty("where", calculateWhereString());
    }

    String calculateWhereString() {
        if (whereClauses == null || whereClauses.isEmpty()) {
            return null;
        }
        return "(" + String.join(" and ", whereClauses) + ")";
    }
}
