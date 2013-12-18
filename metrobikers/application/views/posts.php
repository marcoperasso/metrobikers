<div>
    <?php
    foreach ($posts as $post) {
        ?>
        <hr>
        <div class="postcontainer">
            <div class ="postpublished">Pubblicato da <h5 class="postheader"><?php echo $post->name . ' ' . $post->surname; ?></h5> il <?php echo $post->time ?> 
                <?php if ($post->userid == $user->id) { ?>
                    <a href="#" title ="Elimina post" class="deletepost"><img src="/asset/img/icon_delete.png"/></a>
                    <a href="#" title ="Modifica post" class="editpost"><img src="/asset/img/icon_edit.png"/></a>
        <?php } ?>
            </div>
            <div posttime ="<?php echo $post->time ?>" class ="postbody <?php if ($post->userid == $user->id) echo 'postcontent' ?>"><?php echo html_escape($post->content) ?></div>
        </div>
    <?php
}
?>
</div>
<div id ='missingposts'></div>