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
    $(function()
    {
        $('#loader').bind('inview', function(event, visible) {
            if (visible) {
                $.get("/home/get_more_posts/" + currentPostOffset, function(data) {
                    $("#missingposts").replaceWith(data);
                    currentPostOffset += <?php echo POST_BLOCK_SIZE; ?>;
                    adjustLoaderVisibility();
                });
            }
        });

        currentPostOffset = <?php echo POST_BLOCK_SIZE; ?>;
        totalPosts = '<?php echo $count; ?>';
        adjustLoaderVisibility();
    });
</script>
<div class="col-md-6 "  >
    <h3 class="text-center">Novità dagli ECOmmuters</h3>
    <div class="container">
        <form action="/home/post" method="post">
            <div class="form-group" id="fieldscontainer">
                <div class="form-group">
                    <table class="table">
                        <tr>
                            <td><input type="input" name="content" class="form-control autofocus required" placeholder="Hai qualche novità? Comunicala al gruppo!"/> </td>
                            <td style="width: 20px"><input type="submit" name="submit" id="submit" value="OK" class="btn btn-primary " /></td>
                        </tr>
                    </table>
                </div>
            </div>
        </form>
        <?php
        $this->load->view('posts', array('posts', $posts));
        ?>
        <div id="loader" class="postloader"><img src="/asset/img/loading.gif"/></div>
    </div>
</div>
<div class="col-md-6">
    <h3 class="text-center">ECOmmuters sul territorio</h3>
    <?php $this->load->view('map'); ?>  
</div>


