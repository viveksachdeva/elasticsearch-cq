$(document).ready(function () {
    $.ajax({
        dataType: 'json',
        url: '/bin/generateFacet',
        data: {showAll: "true"},
        success: function (data) {
            var facetData = "<table border='2px solid black' class='table table-striped'><thead><tr><th>Tag Id</th><th>Tag Count</th></tr></thead><tbody>"
            if (data.facets.tagFacet.terms) {
                $.each(data.facets.tagFacet.terms, function () {
                    facetData = facetData + "<tr><td>" + this.term + "</td><td>" + this.count + "</td></tr>"
                })
            }
            facetData = facetData + "</tbody></table>"
            $(".facet-results").html(facetData);
        }
    });

    $('body').on('click', '.facet-tag', function () {
        var facetName = $(this).text()
        performFreeTextSearch(facetName.substr(0, facetName.lastIndexOf("(")));
    });


    $("#search-button").on("click", function () {
        performFreeTextSearch();
    });

    function performFreeTextSearch(facetId) {
        var searchPath = $("#search-root").val();
        var searchFor = $("input[name=search-field]").val();
        var searchData = "<div style='width:70%'><h2>Search Results(In Order of relevancy)</h2>"
        searchData = searchData + "<ul>"
        var filteredPages = "";
//        var searchData = "<table border='2px solid black' class='table table-striped'><thead><tr><th>Page</th><th>Relevancy Score</th></tr></thead><tbody>"
        $.ajax({
            dataType: 'json',
            url: '/bin/searchPages',
            data: {searchTerm: searchFor, facetName: facetId, searchIn: searchPath},
            success: function (data) {
                if (data.hits.total > 0) {
                    var outputjson = data.hits.hits;
                    for (var i = 0; i < outputjson.length; i++) {
                        filteredPages = filteredPages + outputjson[i]._source.docId + ","
                        searchData = searchData + "<li><a target='_blank' href=" + outputjson[i]._source.docId + ".html>" + outputjson[i]._source.titleText + "</a> (" + outputjson[i]._score + ")</li>"
                    }

                }
                else {
                    searchData = searchData + "<div style='clear:both'>No results found</div>"
                }
                searchData = searchData + "</ul></div>"
                $(".search-results").html(searchData);

                $.ajax({
                    dataType: 'json',
                    url: '/bin/generateFacet',
                    data: {"filteredPages": filteredPages},
                    success: function (data) {
                        var facetData = "<h5>Tags</h5><ul>"
                        if (data.facets.tagFacet.terms) {
                            $.each(data.facets.tagFacet.terms, function () {
                                if (this.count > 0)
                                    facetData = facetData + "<li><a href='javascript:void(0)' class='facet-tag''> " + this.term + "(" + this.count + ")</a></li>"
                            })
                        }
                        facetData = facetData + "</ul>"
                        $(".search-facet-results").html(facetData);
                    }
                });

                searchData = searchData + "</div>"

            }
        });
    }


});