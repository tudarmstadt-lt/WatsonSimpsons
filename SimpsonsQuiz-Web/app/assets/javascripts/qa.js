$( document ).ready(function() {
    
    answerIndex= 0;
    
    $('.loading').hide();
    // hide all answers after answerIndex
    $('li[id^="answer"]:gt('+ answerIndex+ ')').hide();
    // show next button if next answer exists
    if($('li[id="answer'+ (answerIndex+ 1)+'"]').length){
            $('#nextAnswer').show();
    }
    
    $('#ask-button').click(function() {
        $(this).resetAsk();
        $('#noAnswer').hide();
     });
    
    $.fn.resetAsk = function() {
        answerIndex= 0;
        $('li[id^="answer"]').hide();
        $('#nextAnswer').hide();
        $('.loading').show();
    };
    
    $('#nextAnswer').click(function() {
        // show next answer
        answerIndex= answerIndex+ 1;
        var answer = $('li[id="answer'+ answerIndex+ '"]');
        answer.slideDown( "slow", function() {
            $("html, body").animate({scrollTop: $("#nextAnswer").offset().top},"slow");
        });
        
        // show next button if there are more answers
        if(!($('li[id="answer'+ (answerIndex+ 1)+'"]').length)){
            $('#nextAnswer').hide();
        }
    });

    
});