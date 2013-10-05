<?php
$user = get_user();
if ($user == NULL) {
        redirect(base_url());
} ?>