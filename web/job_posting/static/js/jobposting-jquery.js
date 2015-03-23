$(document).ready(function() {

        // JQuery code to be added in here.
        // calls the server to delete the selected jobs
        $("#delete").click( function(event) {
        	var values = [];
            	$('#jobsTable').find("input[type=checkbox]:checked").each(function () {
            			var row = $(this);
            			// alert($(this).val());
            			values.push(JSON.stringify({
            				pk : $(this).val()
            			}));
    			});
    		mark_delete('delete', values)
    	});

        // calls the server to mark the selected jobs
    	$("#mark").click( function(event) {
        	var values = [];
            	$('#jobsTable').find("input[type=checkbox]:checked").each(function () {
            			var row = $(this);
            			// alert($(this).val());
            			values.push(JSON.stringify({
            				pk : $(this).val()
            			}));
    			});
    			mark_delete('mark', values)
    		});

    	// helper function to mark or delete 
    	mark_delete = function(type, values){
    		$.ajax({
        		type: 'POST',
        		url: 'mark_delete_jobs/',
        		data: { 'action': type , 'values[]': values,},
        		success: function(response) {
        			//alert(response.success);
        			location.reload();
                    uncheck_all();
    			}
    		});
    	}

        uncheck_all = function(){
            $('tbody tr td input[type="checkbox"]').each(function(){
            $(this).prop('checked', false);
            });
        }
});
