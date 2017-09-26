package at.dccs.liferay.menu;

import java.util.List;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.theme.ThemeDisplay;

public interface MenuSearchLocalService {
	public List<Document> getMenuOfTheWeek(ThemeDisplay themeDisplay, String structureKey, String searchfield, long offset) throws SearchException, ParseException;	
	public List<Document> getMenuOfTheDay (ThemeDisplay themeDisplay, String structureKey, String searchfield, long offset) throws SearchException, ParseException;
}
