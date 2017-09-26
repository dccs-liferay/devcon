package at.dccs.liferay.search;

import java.util.List;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.theme.ThemeDisplay;

public interface RangeSearchLocalService {
	public SearchContext createSearchContext(ThemeDisplay themeDisplay);

	public Sort createLongSort(String fieldName, boolean reverse);
	public Sort createStringSort(String fieldName, boolean reverse);
	
	public DDMStructure getStructure(long companyId, long groupid, String structureKey);
	public String getDDMSearchField(DDMStructure structure, String searchfield);
	
	public BooleanQuery getBasicQuery(ThemeDisplay themeDisplay, DDMStructure structure) throws ParseException;
	public TermRangeQuery createRangeQuery(String field, String lowerTerm, String upperTerm, boolean includesLower, boolean includesUpper);
	public void addExactRequiredTerm(BooleanQuery query, String field, String value) throws ParseException;
	
	public List<Document> search(SearchContext searchContext, BooleanQuery query) throws SearchException;
}
