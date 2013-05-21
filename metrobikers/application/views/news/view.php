<html>
    <?php $this->load->view("templates/header/header.php"); ?>
    <body>
        <?php
        echo '<h2>' . $news_item['title'] . '</h2>';
        echo $news_item['text'];
        ?>       
    </body>

</html>