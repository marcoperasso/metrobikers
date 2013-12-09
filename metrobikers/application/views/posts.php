<?php foreach ($posts as $post) { ?>
    <hr>
    <h5 class="postheader">
        <?php echo $post->name . ' ' . $post->surname; ?>
    </h5><div class ="postpublished">Pubblicato il <?php echo $post->time ?> <a href="#" posttime ="<?php echo $post->time ?>" title ="Elimina post" class="deletepost"><img src="/asset/img/icon_delete.png"/></a></div>
    <div posttime ="<?php echo $post->time ?>" class ="postbody <?php if ($post->userid == $user->id) echo 'changeable postcontent'?>"><?php echo html_escape($post->content) ?></div>
    
    <?php
}
?>
<div id ='missingposts'></div>