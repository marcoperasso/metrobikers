<script type="text/javascript" src="/asset/js/jquery.inview.js">
</script>
<script type="text/javascript" >
    setUpdateUrl("/home/update_post");
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

        window.location.href = '/home/delete_post?posttime=' + encodeURIComponent($(post).attr('posttime'));
    }
    function loadAdditionalPosts()
    {
        $.get("/home/get_more_posts/" + currentPostOffset, function(data) {
            var placeHolder = $("#missingposts");
            var jData = $(data).insertAfter(placeHolder);
            placeHolder.remove();
            currentPostOffset += <?php echo POST_BLOCK_SIZE; ?>;
            adjustLoaderVisibility();

            $(".changeable", jData).each(function() {
                this.tabIndex = window.tab_idx++;
                attachControl(this);
            });
            $(".deletepost", jData).click(function() {
                deletePost(this);
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
            if (e.which === 13) {
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


