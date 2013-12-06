<?php foreach ($posts as $post) { ?>
    <hr>
    <h5 class="postheader">
        <?php echo $post->name . ' ' . $post->surname; ?>
    </h5>
    <div posttime ="<?php echo $post->time ?>" class ="postbody <?php if ($post->userid == $user->id) echo 'changeable postcontent'?>"><?php echo html_escape($post->content) ?></div>
    <div class ="postfooter">Pubblicato il <?php echo $post->time ?></div>
    <?php
}
?>
<div id ='missingposts'></div>