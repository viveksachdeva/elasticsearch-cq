$(document).ready(function () {
    $.ajax({
        dataType: 'json',
        url: '/bin/generateFacet',
        success: function (data) {
            console.log(data.facets.tagFacet.terms)
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
    $("#search-button").on("click", function () {
        var searchFor = $("input[name=search-field]").val();
        var searchData = "<table border='2px solid black' class='table table-striped'><thead><tr><th>Name</th><th>Address</th><th>Relevancy Score</th></tr></thead><tbody>"
        $.ajax({
            dataType: 'json',
            url: '/bin/searchPages',
            data: {searchTerm: searchFor},
            success: function (data) {
                if (data.hits.total > 0) {
                    var outputjson = data.hits.hits;
                    for (var i = 0; i < outputjson.length; i++) {
                        searchData = searchData + "<tr><td>"+outputjson[i]._source.firstname+"</td><td>" + outputjson[i]._source.address + "</td><td>" + outputjson[i]._score + "</td></tr>"
                    }

                }
                searchData = searchData + "</tbody></table>"
                $(".search-results").html(searchData);
            }
        });
    })
});