package at.dccs.liferay.search;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelperUtil;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.QueryTermImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.search.generic.TermRangeQueryImpl;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;

@Component(immediate = true)
public class RangeSearchLocalServiceImpl implements RangeSearchLocalService {

	@Override
	public SearchContext createSearchContext(ThemeDisplay themeDisplay) {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setLayout(themeDisplay.getLayout());
		searchContext.setLocale(themeDisplay.getLocale());
		searchContext.setTimeZone(themeDisplay.getTimeZone());
		searchContext.setUserId(themeDisplay.getUserId());

		return searchContext;
	}

	@Override
	public Sort createLongSort(String fieldName, boolean reverse) {
		Sort sort = SortFactoryUtil.create(fieldName, Sort.LONG_TYPE, reverse);
		return sort;
	}

	@Override
	public Sort createStringSort(String fieldName, boolean reverse) {
		Sort sort = SortFactoryUtil.create(fieldName, Sort.STRING_TYPE, reverse);
		return sort;
	}

	@Override
	public TermRangeQuery createRangeQuery(String field, String lowerTerm, String upperTerm, boolean includesLower, boolean includesUpper) {
		TermRangeQuery rangeQuery = new TermRangeQueryImpl(field, lowerTerm, upperTerm, includesLower, includesUpper);
		return rangeQuery;
	}

	public DDMStructure getStructure(long companyId, long groupid, String structureKey) {
		long classNameId = classnameLocalService_.getClassNameId(JournalArticle.class);		
		return ddmstructureLocalService_.fetchStructure(groupid, classNameId, structureKey);
	}
	
	public BooleanQuery getBasicQuery(ThemeDisplay themeDisplay, DDMStructure structure) throws ParseException {
		BooleanQuery query = new BooleanQueryImpl();
		addExactRequiredTerm(query, "entryClassName", JournalArticle.class.getName());
		addExactRequiredTerm(query, "ddmStructureKey", structure.getStructureKey());
		addExactRequiredTerm(query, "head", "true"); // Only the current version
		addExactRequiredTerm(query, "groupId", String.valueOf(themeDisplay.getScopeGroupId()));
		return query;
	}

	public String getDDMSearchField(DDMStructure structure, String searchfield) {
		return fieldPrefix + "__" + structure.getStructureId() + "__" + searchfield + "_"
				+ structure.getDefaultLanguageId(); // ddm__keyword__12345__MenuDay_en_US
	}
	
	/**
	 * Helper function to create a clean, exact MUST query.
	 */
	public void addExactRequiredTerm(BooleanQuery query, String field, String value) throws ParseException {
		TermQueryImpl termQuery = new TermQueryImpl(new QueryTermImpl(field, value));
		query.add(termQuery, BooleanClauseOccur.MUST);
	}

	@Override
	public List<Document> search(SearchContext searchContext, BooleanQuery query) throws SearchException {
		Hits hits = IndexSearcherHelperUtil.search(searchContext, query);
		return hits.toList();
	}
	
	@Reference
	public void setClassnameLocalService(ClassNameLocalService classnameLocalService) {
		classnameLocalService_ = classnameLocalService;
	}
	
	@Reference
	public void setddmstructureLocalService(DDMStructureLocalService ddmstructureLocalService) {
		ddmstructureLocalService_ = ddmstructureLocalService;
	}
	
	protected static final String fieldPrefix = "ddm__keyword";	
	protected DDMStructureLocalService ddmstructureLocalService_;
	protected ClassNameLocalService classnameLocalService_;
}
