$(document).ready(function() {
        // TODO: Lots of Repitition think about if we can parameterize or something
        // (Or just use one table )

        // JQuery code to be added in here.
        $('#jobsTable').dataTable();
        $('#activeJobsTable').dataTable();
        $('#completedJobsTable').dataTable();
        $('#bidsTable').dataTable();
        $('#id_orders_table tbody tr').formset({
            prefix: 'ordered_items'
        })
        $('[data-toggle=offcanvas]').click(function() {
            $('.row-offcanvas').toggleClass('active');
        });
        /* activate sidebar */
        $('#sidebarmenu').affix({
          offset: {
            top: 50
          }
        });
        
        $("#delete").click( function(event) {
        	var values = [];
            	$('#jobsTable').find("input[type=checkbox]:checked").each(function () {
            			var row = $(this);
            			// alert($(this).val());
            			values.push(JSON.stringify({
            				pk : $(this).val()
            			}));
    			});
    		sendMsgToServer('delete', values)
    	});

        // calls the server to delete the selected active jobs
        $("#delete_active").click( function(event) {
            var values = [];
                $('#activeJobsTable').find("input[type=checkbox]:checked").each(function () {
                        var row = $(this);
                        // alert($(this).val());
                        values.push(JSON.stringify({
                            pk : $(this).val()
                        }));
                });
            sendMsgToServer('delete', values)
        });

        // calls the server to delete the completed jobs
        $("#delete_complete").click( function(event) {
            var values = [];
                $('#completedJobsTable').find("input[type=checkbox]:checked").each(function () {
                        var row = $(this);
                        // alert($(this).val());
                        values.push(JSON.stringify({
                            pk : $(this).val()
                        }));
                });
            sendMsgToServer('delete', values)
        });

        // calls the server to mark the selected jobs

        // calls the server to mark the selected active jobs
        $("#mark_active").click( function(event) {
            var values = [];
                $('#activeJobsTable').find("input[type=checkbox]:checked").each(function () {
                        var row = $(this);
                        // alert($(this).val());
                        values.push(JSON.stringify({
                            pk : $(this).val()
                        }));
                });
                sendMsgToServer('mark', values)
        });

        // called the server to accept a bid
        $("#accept").click( function(event) {
            var values = [];
            var theCheckboxes = $("input[type='checkbox']")
            if (theCheckboxes.filter(":checked").length > 1 ||
                theCheckboxes.filter(":checked").length == 0){
                alert("You are only allowed to accept one bid! " + theCheckboxes.filter(":checked").length
                    + " are selected !");
                return false;
            }else{
                $('#bidsTable').find("input[type=checkbox]:checked").each(function () {
                        var row = $(this);
                        values.push(JSON.stringify({
                            pk : $(this).val()
                        }));
                });
            sendMsgToServer('accept',values)
            }
        });

    	// helper function to mark or delete 
    	sendMsgToServer = function(type, values){
    		$.ajax({
        		type: 'POST',
        		url: 'js_message/',
        		data: { 'action': type , 'values[]': values,},
        		success: function(response) {
        			alert(response.success);
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
