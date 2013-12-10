<div>
    <?php foreach ($posts as $post) { ?>
        <hr>
        <div class ="postpublished">Pubblicato da <h5 class="postheader"><?php echo $post->name . ' ' . $post->surname; ?></h5> il <?php echo $post->time ?> 
            <?php if ($post->userid == $user->id){ ?>
            <a href="#" posttime ="<?php echo $post->time ?>" title ="Elimina post" class="deletepost"><img src="/asset/img/icon_delete.png"/></a>
            <?php } ?>
        </div>
        <div posttime ="<?php echo $post->time ?>" class ="postbody <?php if ($post->userid == $user->id) echo 'changeable postcontent' ?>"><?php echo html_escape($post->content) ?></div>

        <?php
    }
    ?>
</div>
<div id ='missingposts'></div>