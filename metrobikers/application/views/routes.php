<script type="text/javascript">setActiveTab('routes');</script>
<?php
$data = isset($routes) ? array('routes' => $routes) : array();
$this->load->view('map', $data);
?>
            
