$( document ).ready(function() {

    answerGiven = false;
    correctAnswer = "";
    questionID = 0;

    $.fn.renderQuestion = function(quizquestion) {
        $(document).resetRendering();
        $('#question-text').text(quizquestion.question);
        $('#answer1').text(quizquestion.answer1);
        $('#answer2').text(quizquestion.answer2);
        $('#answer3').text(quizquestion.answer3);
        $('#answer4').text(quizquestion.answer4);
        correctAnswer = 'answer' + quizquestion.correctIndex;
        questionID = quizquestion.id;
    };

    $('#answers button').click(function( event ) {
        if(answerGiven)
            return;

        answerGiven = true;
        selectedAnswer = $(this).attr('id');

        $(this).validateAnswer(correctAnswer, selectedAnswer);

    });

    $.fn.resetRendering = function() {
        $('#answers button').addClass('btn-default').removeClass('btn-danger').removeClass('btn-success');
        $('#answers button').prop('disabled', false);
        answerGiven = false;
        correctAnswer = "";
    };

    $.fn.validateAnswer = function(rightAnswer, selectedAnswer) {
        if(selectedAnswer == rightAnswer) {
            $(this).removeClass('btn-default');
            $(this).addClass('btn-success');
            $('#answers button').prop('disabled', true);
            $('#nextquestion').show();
        } else {
            $(this).addClass('btn-danger');
            $('#'+rightAnswer).addClass('btn-success');
            $('#answers button').prop('disabled', true);
        }

    };

    $.fn.noQuestionsLeft = function() {
        $('#questionreview-box').hide();
        $('#answers').hide();
        $('#question-text').text("No further questions available.");
        $('#buttonContainer').hide();
    };

    $('#submitandnextquestion').click(function() {
        var check0=$("#checkboxGroup-0").is(":checked");
        var check1=$("#checkboxGroup-1").is(":checked");
        var check2=$("#checkboxGroup-2").is(":checked");
        var diff=$("#difficultySelector").val();
        if(diff === null){
            $(document).showMessage("Please select a difficulty value!", false);
        } else {
            jsRoutes.controllers.ReviewQuestions.submit(questionID,check0,check1,check2,diff).ajax({
                success : function(data, textStatus) {
                    $(document).showMessage("Review submitted successfully!", true);
                    $(document).updateUserScore();
                    if(textStatus == 'nocontent') {
                       // behavior if no more questions
                       $(document).noQuestionsLeft();
                    } else {
                        if(data !== null)
                            $(document).renderQuestion(data);
                    }
                },
                error : function(data) {
                    $(document).showMessage("Error while submitting the review!", false);
                }
            });
        }
        $(document).resetReviewForm();
    });

    $('#nextquestion').click(function() {
        $(document).resetReviewForm();
        jsRoutes.controllers.ReviewQuestions.nextQuestion().ajax({
            success : function(data, textStatus) {
                if(textStatus == 'nocontent') {
                   // behavior if no more questions
                   $(document).noQuestionsLeft();
                } else {
                    if(data !== null)
                        $(document).renderQuestion(data);
                }
            },
            error : function(data) {
                    $(document).showMessage("Error while submitting the review!", false);
            }
        });
    });

    $.fn.resetReviewForm = function() {
        $("#checkboxGroup-0").prop("checked", false);
        $("#checkboxGroup-1").prop("checked", false);
        $("#checkboxGroup-2").prop("checked", false);
        $("#difficultySelector").val(0);
    };

    $.fn.updateUserScore = function() {
        var newScore = parseInt($("#user-score-value").text()) + 1;
        $("#user-score-value").text(newScore);
    };

    $.fn.showMessage = function(message, success){
        $("#review-alert").show();
        if(success)
            $("#review-alert").addClass("alert-success").removeClass("alert-danger");
        else
            $("#review-alert").addClass("alert-danger").removeClass("alert-success");
        $("#review-alert").html(message);
        $('#review-alert').animate({opacity: 1.0}, 4000).fadeOut('slow', function() {
            $(this).hide();
        });
    };

    $('#review-alert').hide();
    $('#nextquestion').trigger('click');

});