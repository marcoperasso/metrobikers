<script type="text/javascript" src="/asset/js/jquery.inview.js">
</script>
<script type="text/javascript" >
    setUpdateUrl("/home/update_post");
    function PostControl()
    {
        Control.call(this);
        var thisObj = this;

        this.getPostData = function()
        {
            var data = {};
            data.postcontent = thisObj.unformatData();
            data.posttime = thisObj.getObj().attr('posttime');
            return data;
        };

        this.activateObj = function() {
            thisObj.getObj()[0].editField = thisObj.editField;
        };

        this.onAfterSetValue = function() {
            thisObj.getObj().html(adjustContent(thisObj.getObj().text()));
        };
        this.createInput = function()
        {
            var inputControl = $("<textarea class='autoedit'></textarea>");
            inputControl.val(thisObj.getObj().text());
            inputControl.blur(thisObj.save);
            return inputControl;
        };
    }
    PostControl.prototype = new Control();

    function adjustLoaderVisibility()
    {
        if (currentPostOffset < totalPosts)
            $('#loader').show();
        else
            $('#loader').hide();
    }
    function deletePost(post)
    {
        if (!confirm("Vuoi davvero cancellare questo elemento?"))
            return;

        var cnt = $(post).closest('.postcontainer');
        window.location.href = '/home/delete_post?posttime=' + encodeURIComponent($('.postbody', cnt).attr('posttime'));
    }
    function editPost(post)
    {
        var cnt = $(post).closest('.postcontainer');
        $('.postbody', cnt)[0].editField();
    }
    function adjustContent(str)
    {
        if (!window.rexAnchor)
            window.rexAnchor = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
        str = str.replace(window.rexAnchor, function($0) {
            return '<a href="' + $0 + '">' + $0 + '</a>';
        });

        if (!window.brAnchor)
            window.brAnchor = /(\n)|(\r\n)/ig;
        str = str.replace(window.brAnchor, function() {
            return '<br>';
        });

       /* if (!window.ecommutersAnchor)
            window.ecommutersAnchor = /<ECM(\w+)>\w+<\/<ECM>/ig;
        str = str.replace(window.ecommutersAnchor, function($1, $2) {
            return $1 + '.' + $2;
        });*/
        return str;
    }
    function loadAdditionalPosts()
    {
        $.get("/home/get_more_posts/" + currentPostOffset, function(data) {
            var placeHolder = $("#missingposts");
            var jData = $(data).insertAfter(placeHolder);
            placeHolder.remove();
            currentPostOffset += <?php echo POST_BLOCK_SIZE; ?>;
            adjustLoaderVisibility();

            $(".postcontent", jData).each(function() {
                this.tabIndex = window.tab_idx++;
                var ctrl = new PostControl();
                ctrl.setObj($(this));
                ctrl.activateObj();
            });
            $(".deletepost", jData).click(function() {
                deletePost(this);
            });

            $(".editpost", jData).click(function() {
                editPost(this);
            });

            $(".postbody", jData).each(function() {
                this.innerHTML = adjustContent(this.innerHTML);
            });
        });
    }
    $(function()
    {
        $('#loader').bind('inview', function(event, visible) {
            if (visible) {
                loadAdditionalPosts();
            }
        });

        $('#content').keypress(function(e) {
            var jObj = $(this);
            if (e.which === 13 && !e.shiftKey) {
                jObj.closest('form').find('.btn-default').click();
                e.preventDefault();
            }
        });

        currentPostOffset = 0;
        totalPosts = '<?php echo $count; ?>';
        loadAdditionalPosts();
    });
</script>
<style type="text/css">
    textarea.autoedit
    {
        display: block;
        width: 100%;
    }
</style>

<div class="col-md-6 "  >
    <h3 class="text-center">Novità dagli ECOmmuters</h3>
    <div class="container">
        <form action="/home/create_post" method="post">
            <div class="form-group" id="fieldscontainer">
                <div class="form-group">
                    <table class="table">
                        <tr>
                            <td><textarea id="content" name="content" class="form-control autofocus required" placeholder="Hai qualche novità? Comunicala al gruppo!"></textarea> </td>
                            <td style="width: 20px"><input type="submit" name="submit" id="submit" value="OK" class="btn btn-primary btn-default" /></td>
                        </tr>
                    </table>
                </div>
            </div>
        </form>
        <div id ='missingposts'></div>
        <div id="loader" class="postloader"><img src="/asset/img/loading.gif"/></div>
    </div>
</div>
<div class="col-md-6">
    <h3 class="text-center">ECOmmuters sul territorio</h3>
    <?php $this->load->view('map'); ?>  
</div>


