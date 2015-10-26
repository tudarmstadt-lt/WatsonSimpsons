$( document ).ready(function() {

    SCORE_PER_RIGHT_ANSWER_SP = 10;
    SCORE_PER_WRONG_ANSWER_SP = 0;
    TIME_PER_ANSWER_SP = 20; // in seconds

    SCORE_PER_RIGHT_ANSWER = 20;
    SCORE_PER_2ND_RIGHT_ANSWER = 15;
    SCORE_PER_WRONG_ANSWER = -10;

    TIME_FOR_BUZZING = 10;
    TIME_PER_ANSWER = 15; // in seconds
    TIME_PER_2ND_ANSWER = 5;


    multiPlayer = false;
    playerBuzzed = false;
    answerGiven = false;
    opponentTurn = false;
    currentPlayer = "";
    correctAnswer = "";

    scoreA = 0;
    scoreB = 0;
    scoreS = 0;

    $.fn.getOpponent = function() {
        if(currentPlayer == 'A')
            return 'B';
        else
            return 'A';
    };

    $.fn.resetRendering = function() {
        $('#buzz-info').hide();
        $('.player-info').removeClass('alert-buzzed');
        $('#nextquestion').hide();
        $('#answers button').addClass('btn-default').removeClass('btn-danger').removeClass('btn-success');
        $('#progress-timer').css('width', 0);

        if(multiPlayer) {
            $('#answers button').prop('disabled', true);
            playerBuzzed = false;
        } else {
            $('#answers button').prop('disabled', false);
            playerBuzzed = true;
        }

        answerGiven = false;
        opponentTurn = false;
        correctAnswer = "";
        currentPlayer = "";
    };

    $.fn.renderQuestion = function(quizquestion) {
        $(document).resetRendering();
        $('#question-text').text(quizquestion.question);
        $('#answer1').text(quizquestion.answer1);
        $('#answer2').text(quizquestion.answer2);
        $('#answer3').text(quizquestion.answer3);
        $('#answer4').text(quizquestion.answer4);
        correctAnswer = 'answer' + quizquestion.correctIndex;
        $("#progress-timer").runTimer(TIME_FOR_BUZZING * 1000, $(document).timerOutNotBuzzed);
    };

    $.fn.buzzed = function(player) {
        playerBuzzed = true;
        $('.player-info').removeClass('alert-buzzed');
        $('#buzz-info #player-name').text('Player '+player);
        $('#buzz-info').show();
        $('#player'+player+'-info').addClass('alert-buzzed');
        $('#answers button').prop('disabled', false);
        $("#progress-timer").stop(true).css('width', 0);
        if(opponentTurn)
            $("#progress-timer").runTimer(TIME_PER_2ND_ANSWER * 1000, $(document).timerOutNot2ndAnswered);
        else
            $("#progress-timer").runTimer(TIME_PER_ANSWER * 1000, $(document).timerOutNotAnswered);
    };

    $.fn.validateAnswer = function(rightAnswer, selectedAnswer) {
        if(selectedAnswer == rightAnswer) {
            if(!multiPlayer) {
                $(document).updateScore(SCORE_PER_RIGHT_ANSWER_SP);
            } else {
                if(opponentTurn)
                    $(document).updateScore(SCORE_PER_2ND_RIGHT_ANSWER);
                else
                    $(document).updateScore(SCORE_PER_RIGHT_ANSWER);
            }
            $(this).removeClass('btn-default');
            $(this).addClass('btn-success');
            $('#answers button').prop('disabled', true);
            $('#nextquestion').show();
        } else if(multiPlayer && !opponentTurn) {
            $(document).updateScore(SCORE_PER_WRONG_ANSWER);
            $(this).addClass('btn-danger');
            opponentTurn = true;
            answerGiven = false;
            currentPlayer = $(document).getOpponent();
            $(document).buzzed(currentPlayer);
            $(this).prop('disabled', true);
        } else {
            $(this).addClass('btn-danger');
            $('#'+rightAnswer).addClass('btn-success');
            $('#answers button').prop('disabled', true);
            $('#nextquestion').show();

        }

    };

    $.fn.updateScore = function(points) {
        if(!multiPlayer) {
            scoreS += points;
            $('#playerS-score').text(scoreS);
        } else {
            if(currentPlayer == 'A') {
                scoreA += points;
                $('#playerA-score').text(scoreA);
            } else if(currentPlayer == 'B') {
                scoreB += points;
                $('#playerB-score').text(scoreB);
            }
        }
    };

    $.fn.runTimer = function (duration, complFunc) {
        $(this).css('width', 0).css('background-color', '#5bc0de').animate(
          {
            width: "100%"
          },
          duration,
          "linear",
          complFunc
        );//end animate
    };

    $.fn.timerOutNotBuzzed = function() {
        $(this).css('background-color', '#843534');
        $('#answers button').prop('disabled', true);
        $('#nextquestion').show().prop('disabled', false);
    };

    $.fn.timerOutNotAnswered = function() {
        $(this).css('background-color', '#843534');
        $('#no-answer').validateAnswer(correctAnswer, 'no-answer');
    };

    $.fn.timerOutNot2ndAnswered = function() {
        $(this).css('background-color', '#843534');
        $('#answers button').prop('disabled', true);
        $('#nextquestion').show().prop('disabled', false);
    };

    $.fn.finishedQuiz = function() {
            if(!multiPlayer) {
                if(scoreS === 0) {
                    $('#game-result').html("Oh no! You didn't score any points.");
                } else {
                    $('#game-result').html("Congratulations! You scored <strong>"+scoreS+"</strong> points!");
                }
            } else {
                if(scoreA > scoreB) {
                    $('#game-result').html("Congratulations! Player <strong>A</strong> wins!");
                } else if(scoreA < scoreB) {
                    $('#game-result').html("Congratulations! Player <strong>B</strong> wins!");
                } else {
                    $('#game-result').html("It's a draw!");
                }
                $('#buzz-info').hide();
                $('.player-info').removeClass('alert-buzzed');
            }
            $('#quiz-gameplay').hide();
            $('#quiz-finish-info').show();
        };

    $('#answers button').click(function( event ) {
        if(answerGiven || !playerBuzzed)
            return;

        answerGiven = true;
        selectedAnswer = $(this).attr('id');
        $("#progress-timer").stop();

        $(this).validateAnswer(correctAnswer, selectedAnswer);

    });

    $(document).keydown(function(event) {
        if(playerBuzzed)
            return;
        if(event.which == 83) { // s
            playerBuzzed = true;
            currentPlayer = 'A';
            $(document).buzzed(currentPlayer);
        } else if(event.which == 75) { // k
            playerBuzzed = true;
            currentPlayer = 'B';
            $(document).buzzed(currentPlayer);
        }

    });

    $('#nextquestion').click(function() {

        jsRoutes.controllers.Quiz.nextQuestion().ajax({
            success : function(data, textStatus) {
                if(textStatus == 'nocontent') {
                   // behavior if no more questions
                   $(document).finishedQuiz();
                } else {
                    if(data !== null)
                        $(document).renderQuestion(data);
                }
            },
            error : function(data) {
                alert('Error!');
            }
        });
    });

    $('#startgame-sp').click(function() {
        multiPlayer = false;
        scoreS = 0;
        $('#quiz-startpage').hide();
        $('.mp').hide();
        $('#quiz-gamepage').show();
        $(document).resetRendering();
        $('#nextquestion').trigger('click');
    });

    $('#startgame-mp').click(function() {
        multiPlayer = true;
        scoreA = 0;
        scoreB = 0;
        $('#quiz-startpage').hide();
        $('.sp').hide();
        $('#quiz-gamepage').show();
        $(document).resetRendering();
        $('#nextquestion').trigger('click');
    });

    // Initialization:
    $('#quiz-gamepage').hide();
    $('#quiz-finish-info').hide();

});