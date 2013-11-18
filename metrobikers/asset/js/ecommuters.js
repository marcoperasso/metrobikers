/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(function() {
    setContentHeight();
    $(window).resize(setContentHeight);
    $(".changeable").click(editField);
});
function setContentHeight()
{
    $("body").css("min-height", ($(window).height()) + "px");
}
function onPersistField()
{
    var obj = $(this.original);
    var input = $(this);
    var modified = obj.text() !== input.val();
    obj.click(editField);
    input.replaceWith(obj);
    if (modified)
    {
        var data = {};
        data[this.original.attr("name")] = unformatData(obj, input);
        $.post("/user/update", data, function(res) {
            if (res.result)
                obj.text(formatData(obj, input));
        }, 'json');
    }
}
function formatData(obj, input)
{
    if (obj.hasClass('gendercontent'))
    {
        return $(':selected', input).text();
    }
    return input.val();
}
function unformatData(obj, input)
{
    if (obj.hasClass('datecontent'))
    {
        var currentDate = $.datepicker.parseDate('dd/mm/yy', input.val());
        return currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
    }
    return input.val();
}
function restore()
{
    var obj = $(this.original);
    var input = $(this);
    obj.click(editField);
    input.replaceWith(obj);
}
function getInput(obj)
{
    if (obj.hasClass('datecontent'))
    {
        var input = $("<input type='text'></input>");
        input.datepicker({"autoSize": true, "dateFormat": "dd/mm/yy", "onSelect": onPersistField});
        return input;
    }
    if (obj.hasClass('gendercontent'))
    {
        var html = "<div style='display:inline;'>";
        var items = ['Non specificato', 'Femmina', 'Maschio'];
        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            var selected = item === obj.text();
            html += '<input type="radio" value="' + i + '"' + (selected ? ' checked' : '') + ' name="' + obj.attr('name') + '">' + item + "</input>";
        }
        html += "</div>"
        var input = $(html);
        $('input', input).change(function(){
            input()
        });
        input.blur(restore);
        return input;
    }
    var input = $("<input type='text'></input>");
    input.blur(onPersistField);
    return input;
}
function editField()
{
    var obj = $(this);

    var input = getInput(obj);
    input.val(obj.text());
    $('input', input).each(function(){
         this.original = obj;
    });
    input.each(function(){
         this.original = obj;
    });
    obj.replaceWith(input);
    input.focus();

}