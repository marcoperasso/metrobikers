<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title><?php echo $page_title; ?></title>
    </head>

    <body>
        <div class="content">
            <?php $this->load->view('templates/visualheader'); ?>
            <div class="body row">
                <div><?php $this->load->view($view_name); ?></div>
            </div>
            <?php $this->load->view('templates/footer'); ?>
        </div>
    </body>
</html>