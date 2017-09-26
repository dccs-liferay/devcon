package at.dccs.liferay.menu;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import at.dccs.liferay.search.RangeSearchLocalService;
import at.dccs.liferay.search.RangeSearchLocalServiceImpl;

/*
 * Helper class to search for journal article entries of a week
 * Depends on dccs RangeSearchLocalService
 */
@Component(immediate = true)
public class MenuSearchLocalServiceImpl implements MenuSearchLocalService {
	
	/*
	 * This method searches for webcontent with the following properties:
	 * Structure == structureKey
	 * offset == 0: Date in search field is in the current week
	 * offset +N: Date is in the Nth week after the current
	 * offset -N: Date is in the Nth week before the current
	 * aka: offset -1 == last week, offset +1 == next week
	 */
	public List<Document> getMenuOfTheWeek(ThemeDisplay themeDisplay, String structureKey, String searchfield, long offset) throws ParseException, SearchException {
		final DDMStructure structure = rangeSearch_.getStructure(themeDisplay.getCompanyId(), themeDisplay.getCompanyGroupId(), structureKey);
		final BooleanQuery query = rangeSearch_.getBasicQuery(themeDisplay, structure);

		LocalDate today = LocalDate.now();
		String monday = getWeekStart(today);
		String sunday = getWeekEnd(today);

		String field = rangeSearch_.getDDMSearchField(structure, searchfield);
		TermRangeQuery rangeQuery = rangeSearch_.createRangeQuery(field, monday, sunday, true, true);
		query.add(rangeQuery, BooleanClauseOccur.MUST);
		
		SearchContext searchContext = rangeSearch_.createSearchContext(themeDisplay);
		Sort sort = rangeSearch_.createLongSort(field, false);	
		searchContext.setSorts(sort);
		
		List<Document> docs = rangeSearch_.search(searchContext, query);
		
		return docs;
	}
	/*
	 * This method searches for webcontent with the following properties:
	 * Structure == structureKey
	 * offset == 0: Date in search field is today
	 * offset +N: Date is the Nth day after today
	 * offset -N: Date is in the Nth day before today
	 * aka: offset -1 == yesterday, offset +1 == tomorrow
	 */
	public List<Document> getMenuOfTheDay(ThemeDisplay themeDisplay, String structureKey, String searchfield, long offset) throws SearchException, ParseException {
		final DDMStructure structure = rangeSearch_.getStructure(themeDisplay.getCompanyId(), themeDisplay.getCompanyGroupId(), structureKey);
		final BooleanQuery query = rangeSearch_.getBasicQuery(themeDisplay, structure);
				
		rangeSearch_.addExactRequiredTerm(query, rangeSearch_.getDDMSearchField(structure, searchfield), getDayAsSAtring(LocalDate.now().plusDays(offset)));
		
		SearchContext searchContext = rangeSearch_.createSearchContext(themeDisplay);
		List<Document> docs = rangeSearch_.search(searchContext, query);
		return docs;
	}

	// We would love to do an elasticsearch range search with date parameters like now/d or now/w here,
	// but by default liferay indexes dates as strings.
	// Adding da type mapping like the following is too much for the example.

	// ### Override Type Mappings Sample ###
	/*
	{
	    "LiferayDocumentType" : {
	        "properties" : {
	          "ddm__keyword__<ID>__MenuDay_en_US" : {
	            "type" : "date",
	            "format" : "strict_date_optional_time||epoch_millis",
		    	"store" : true
	          },
	          "ddm__keyword__<ID>__MenuDay_en_US_sortable" : {
	            "type" : "date",
	            "format" : "strict_date_optional_time||epoch_millis",
		    	"store" : true
	          }
	        }
	    }
	}
	 */
	protected String getWeekStart(LocalDate day) {
		LocalDate monday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return monday.format(dateformatter);
	}

	protected String getWeekEnd(LocalDate day) {
		LocalDate sunday = day.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		return sunday.format(dateformatter);
	}
	
	protected String getDayAsSAtring(LocalDate day) {
		return day.format(dateformatter);
	}
	
	@Reference
	public void setRangeSearchLocalService(RangeSearchLocalService rangeSearch) {
		rangeSearch_ = rangeSearch;
	}
	
	protected RangeSearchLocalService rangeSearch_ = new RangeSearchLocalServiceImpl();
	
	private static final DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
