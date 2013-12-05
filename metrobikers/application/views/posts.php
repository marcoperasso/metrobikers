<?php foreach ($posts as $post) { ?>
    <hr>
    <h5 class="postheader">
        <?php echo $post->name . ' ' . $post->surname; ?>
    </h5>
    <div class ="postbody <?php if ($post->userid == $user->id) echo 'changeable gendercontent'?>"><?php echo html_escape($post->content) ?></div>
    <div class ="postfooter">Pubblicato il <?php echo $post->time ?></div>
    <?php
}
?>
<div id ='missingposts'></div>