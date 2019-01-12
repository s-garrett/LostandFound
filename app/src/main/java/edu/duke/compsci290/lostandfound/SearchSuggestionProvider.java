package edu.duke.compsci290.lostandfound;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "edu.duke.compsci290.lostandfound.SearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES| DATABASE_MODE_2LINES;

    public SearchSuggestionProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }

}
