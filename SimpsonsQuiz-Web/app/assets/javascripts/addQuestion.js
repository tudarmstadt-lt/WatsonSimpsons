$( document ).ready(function() {

	var next_individual_id = 5;

	$('.loading').hide();
	$('#message-box').hide();

	$('#ask-button').click(function() {
		$('.loading').show();
	});

    $('#addOwnAnswerButton').click(function() {
        $('#rowv'+next_individual_id).slideDown( "slow", function() {
            $("html, body").animate({scrollTop: $(".addquestion-footer").offset().top},"slow");
        });

        if(next_individual_id > 1)
            next_individual_id--;
        else
            $(this).hide();
    });

    $("form#addQuestionForm").submit(function( event ) {
        if($('#wrongAnswer1').val() === "" || $('#wrongAnswer2').val() === "" || $('#wrongAnswer3').val() === "" ) {
            $('#message-box').html("Selected not enough false answers!").addClass('alert-warning').show();
        } else if($('#rightAnswer').val() === "") {
            $('#message-box').html("The correct answer is missing!").addClass('alert-warning').show();
        } else if($('#questionSend').val() === "") {
            $('#message-box').html("The question text is missing!").addClass('alert-warning').show();
        } else {
            return true;
        }
        $('#saveQuestionButton').addClass('btn-warning').removeClass('btn-primary');
        event.preventDefault();

    });

});

function pressCorrectAnswer(index, answer, size) {
	var newRightAnswer;

	if(index < 0) {
		newRightAnswer = $('#individual'+index).val();
	} else {
		newRightAnswer = answer;
    }

	if(newRightAnswer === "") {
		alert("Please type in an answer into the empty field!");
	} else {
        var currID = "#row"+index;
        var currColor = "#color"+index;

        $('#questionSend').val($('#watsonquestion').val());

        if($(currColor).val() == "green") {
            $('#rightAnswer').val("");
            $(currID).parent().removeClass("marked-correct marked-false");
            $(currColor).val("white");
        } else {

            switch ($(currColor).val()) {
                case "red1":
                    $('#wrongAnswer1').val("");
                    break;
                case "red2":
                    $('#wrongAnswer2').val("");
                    break;
                case "red3":
                    $('#wrongAnswer3').val("");
                    break;
            }

            for (var i = -5; i < size; i++) {
                if($('#color'+i).val() == "green") {
                    $('#row'+i).parent().removeClass("marked-correct marked-false");
                    $('#color'+i).val("white");
                }
            }

            $(currID).parent().removeClass("marked-false").addClass("marked-correct");
            $('#rightAnswer').val(newRightAnswer);
            $(currColor).val("green");
        }
	}
}

function pressFalseAnswer(index, answer) {
	var newFalseAnswer;

	if(index < 0) {
		newFalseAnswer = $('#individual'+index).val();
	} else {
		newFalseAnswer = answer;
    }

	if(newFalseAnswer === "") {
		alert("Please type in an answer into the empty field!");
	} else {
        var currID = "#row"+index;
        var currColor = "#color"+index;

        $('#questionSend').val($('#watsonquestion').val());

        if($(currColor).val() != "green" && $(currColor).val() != "white") {

            switch ($(currColor).val()) {
                case "red1":
                    $('#wrongAnswer1').val("");
                    break;
                case "red2":
                    $('#wrongAnswer2').val("");
                    break;
                case "red3":
                    $('#wrongAnswer3').val("");
                    break;
            }

            $(currColor).val("white");
            $(currID).parent().removeClass("marked-correct marked-false");

        } else {

            var colID=0;
            if($('#wrongAnswer1').val() === "")
                colID=1;
            if($('#wrongAnswer2').val() === "")
                colID=2;
            if($('#wrongAnswer3').val() === "")
                colID=3;

            if(colID === 0) {
                alert("You have already chosen three false answers!");
            } else {
                if($(currColor).val() == "green")
                    $('#rightAnswer').val("");

                $('#wrongAnswer'+colID).val(newFalseAnswer);
                $(currColor).val("red"+colID);
                $(currID).parent().removeClass("marked-correct").addClass("marked-false");
            }
        }
	}
}