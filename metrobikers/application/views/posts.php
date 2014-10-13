<div>
    <?php

    function time_elapsed_string($ptime) {
        $etime = time() - $ptime;

        if ($etime < 1) {
            return 'adesso';
        }

        $a = array(31104000 => array('anni', 'anno'),
            2592000 => array('mesi', 'mese'),
            86400 => array('giorni', 'giorno'),
            3600 => array('ore', 'ora'),
            60 => array('minuti', 'minuto'),
            1 => array('secondi', 'secondo'),
        );

        foreach ($a as $secs => $str) {
            $d = $etime / $secs;
            if ($d >= 1) {
                $r = round($d);
                return $r . ' ' . ($r > 1 ? $str[0] : $str[1]) . ' fa';
            }
        }
    }

    foreach ($posts as $post) {
        ?>
        <hr> 
            
        <div class="postcontainer">
            <table style="margin: 0">
                <tr>
                    <td style="vertical-align: top"><img alt="Foto" src="<?php echo get_user_photo($post->userid) ?>" style="display: inline-block; width:50px;height: 50px; margin: 2px 20px 2px 2px"/></td>
                    <td> <div class ="postpublished">Pubblicato da <h5 class="postheader"><?php echo $post->name . ' ' . $post->surname; ?></h5> <?php echo time_elapsed_string(strtotime($post->time)) ?> 
                <?php if ($post->userid == $user->id) { ?>
                    <a href="#" title ="Elimina post" class="deletepost"><img src="asset/img/icon_delete.png"/></a>
                    <a href="#" title ="Modifica post" class="editpost"><img src="asset/img/icon_edit.png"/></a>
                <?php } ?>
            </div>
            <div posttime ="<?php echo $post->time ?>" class ="postbody <?php if ($post->userid == $user->id) echo 'postcontent' ?>"><?php echo html_escape($post->content) ?></div></td>
                </tr>
            </table>
          
        </div>
        <?php
    }
    ?>
</div>
<div id ='missingposts'></div>