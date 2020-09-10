<!DOCTYPE html>
<html>
<head>

</head>
<body>

<style>
    #cx-report {
        animation: fadein 2s;
    }

    @keyframes fadein {
        from {
            opacity: 0;
        }
        to {
            opacity: 1;
        }
    }

    .aui-group > .aui-item {
        -moz-box-sizing: border-box;
        box-sizing: border-box;
        display: table;
        margin: 0;
    }

    .cx-report .recurrent-legend {
        margin-right: 15px;
    }

    .cx-report .legend-color-box.recurrent-legend-color {
        background-color: #372F51;
    }

    .cx-report .legend-color-box.new-legend-color {
        background: linear-gradient(45deg, white 25%, #373050 25%, #373050 50%, white 50%, white 75%, #373050 75%);
        background-size: 4px 4px;
    }

    .cx-report .legend-color-box {
        width: 16px;
        height: 16px;
        margin-right: 10px;
    }

    .cx-report .legend-text {
        color: #9B9B9B;
        font-family: Roboto, sans-serif;
        font-size: 14px;
        line-height: 16px;
    }

    .cx-report .legend-item {
        display: flex;
        margin-top: auto;
        margin-bottom: auto;
        margin-left: auto;
    }

    .cx-report .chart-legend {
        display: inline-flex;
        height: 100%;
        margin: auto;
    }

    .cx-report .chart-legend-container {
        align-items: center;
        width: 50%;
        text-align: right;
    }

    .threshold-exceeded-compliance {
        width: 50%;
    }

    .cx-report .results-report .sast-summary.chart-large .top-of-chart {
        max-width: 709px;
    }

    .cx-report .top-of-chart {
        display: flex;
        max-width: 475px;
        min-width: 330px;
        width: 100%;
    }

    .cx-report .results-report .sast-summary.chart-large .new-bar-title-container {
        margin-left: 25%;
    }

    .cx-report .new-bar-title-container {
        font-weight: bold;
        background-color: #f2f2f2;
        text-align: center;
        border-radius: 8px;
        width: fit-content;
        line-height: 19px;
        padding: 1px 5px;
        margin: auto;
        color: #373050;
    }

    .cx-report .bar-title-wrapper {
        position: absolute;
    }

    .cx-report .results-report .sast-summary.chart-large .bar-title-wrapper {
        width: 100%;
    }

    .cx-report .new-scans {
        background: linear-gradient(45deg, white 25%,
        #373050 25%, #373050 50%,
        white 50%, white 75%,
        #373050 75%);
        background-size: 4px 4px;
    }

    .cx-report .recurrent-scans {
        background-color: #373050;
    }

    .cx-report .summary-results {
        display: flex;
        position: relative;
        width: 100%;
        min-width: 635px;
    }

    .cx-report .results-report .osa-summary {
        margin: 0;
        position: relative;
        width: 50%;
        margin-left: 3%;
    }

    .cx-report .aui-item.details {
        width: 530px;
    }

    .cx-report .results-report {
        max-width: 1196px;
    }

    .cx-report {
        font-family: 'Roboto', sans-serif;
        margin-right: 35px;
        margin-left: 35px;
        max-width: 1196px;
    }

    .cx-report .cx-report-title {
        font-size: 32px;
        font-weight: 600;
        display: inline-block;
        padding-top: 10px;
        padding-bottom: 10px;
        width: 100%;
        border-bottom: solid 2px #373050;
        color: #373050;
    }

    .cx-report .summary-title-text {
        white-space: nowrap;
        font-weight: bold;
    }

    .cx-report .results-report .summary-section {
        display: flex;
        padding-top: 34px;
        padding-bottom: 60px;
        position: relative;
    }

    .cx-report .sast-failed-error {
        height: 512px;
        width: 100%;
        background-color: #F2F2F2;
        z-index: 3;
        position: relative;
    }

    .cx-report .sast-error-container {
        position: absolute;
        right: 50%;
        top: 50%;
        transform: translate(0, -50%);
        text-align: center;
    }

    .cx-report .sast-error-text {
        color: #382F50;
        font-family: Roboto, sans-serif;
        font-size: 15px;
        line-height: 22px;
        margin-top: 17px;
    }

    .cx-report .results-report .summary-report-title {
        font-size: 24px;
        font-weight: 400;
        padding-bottom: 21px;
        color: #373050;
    }

    .cx-report .results-report .sast-summary {
        position: relative;
        margin-right: 3%;
        width: 50%;
    }

    .cx-report .results-report .detailed-report .report-link {
        color: #4A90E2;
        font-size: 14px;
        padding: 15px 2px;
    }

    .cx-report .results-report .title-links {
        display: flex;
        color: #4A90E2;
        font-size: 14px;
        padding: 15px 2px;
        align-items: center;
    }

    .cx-report .results-report .summary-section .title-links {
        border-bottom: solid 1px #d5d5d5;
    }

    .cx-report .results-report .summary-section .sast .title-links {
        max-width: 475px;
    }

    .cx-report .link-icon {
        align-items: center;
        display: inline-flex;
    }

    .cx-report .summary-section .link-to-result {
        display: inline-flex;
        align-items: center;
    }

    .cx-report .link-to-result {
        display: flex;
        align-items: center;
    }

    .cx-report .summary-link-text {
        padding: 0 8px;
    }

    .cx-report .link-text {
        padding: 0 8px;
    }

    .cx-report .results-report .sast-summary.chart-large .title-links {
        max-width: 100%;
    }

    .cx-report .results-report .sast-summary.chart-large {
        width: 100%;
        margin-bottom: 50px;
        margin-left: 24%;
        max-width: 709px;
    }

    .cx-report .results-report .sast-summary.chart-large .chart {
        max-width: 709px;
        margin-right: auto;
        margin-left: auto;
    }

    .cx-report .results-report .sast-summary.chart-large .chart li span {
        max-width: 53px;
    }

    .cx-report .results-report .sast-summary.chart-large .chart .bar-title-container {
        margin-left: 27%;
        padding-top: 21px;
        display: inline-flex;
        text-align: center;
        word-wrap: break-word;
        color: #444444;
    }

    .cx-report .results-report .sast-summary.chart-large .chart .bar-title {
        text-align: center;
        width: 100%;
    }

    .cx-report .results-report .chart {
        display: table;
        table-layout: fixed;
        max-width: 475px;
        min-width: 330px;
        width: 100%;
        height: 240px;
        margin-top: 0;
        padding: 0;
        background-image: linear-gradient(to top, #d5d5d5, #ffffff 2%);
        background-size: 100% 70px;
        background-position: left bottom;
        border-bottom: solid #979797 2px;
    }

    .cx-report .results-report .chart li {
        position: relative;
        display: table-cell;
        vertical-align: bottom;
        height: 238px;
    }

    .cx-report .results-report .chart li span {
        max-width: 20px;
        /*overflow: hidden;*/
    }

    .cx-report .results-report .chart span {
        margin-left: 25%;
        display: block;
        animation: draw 1s ease-in-out;
    }

    .cx-report .results-report .sast-summary.chart-large .chart .bar-count {
        /*width: 53px;*/
    }

    .cx-report .results-report .chart .bar-count {
        margin-left: 4px;
        font-size: 14px;
    }

    .cx-report .results-report .chart .bar-title {
        font-size: 14px;
        white-space: nowrap;
    }

    .cx-report .results-report .bar-title-icon svg {
        position: absolute;
    }

    .cx-report .results-report .sast-summary.chart-large .bar-title-icon {
        width: 30px;
    }

    .cx-report .results-report .bar-title-icon {
        margin-right: 6px;
        height: 20px;
        width: 16px;
        text-align: left;
    }

    .cx-report .results-report .chart .bar-title-container {
        margin-left: 25%;
        transform: translate(-25%, 0);
        padding-top: 21px;
        display: inline-flex;
        text-align: center;
        word-wrap: break-word;
        color: #444444;
        align-items: center;
    }

    .cx-report .results-report .chart .bar-3,
    .cx-report .results-report .chart .bar-2,
    .cx-report .results-report .chart .bar-1 {
        position: relative;
        background-color: #373050;
    }

    .threshold-exceeded,
    .threshold-compliance,
    .policy-compliance {
        min-width: 100%;
        display: inline-flex;
        font-size: 14px;
        font-weight: normal;
        border-radius: 2px;
        padding: 4px 9px;
    }

    .threshold-exceeded {
        background-color: #DA2945;
        color: white;
        border-radius: 2px;
        font-weight: bold;
    }

    .policy-compliance {
        border-radius: 2px;
        font-weight: bold;
    }

    .threshold-exceeded-icon,
    .threshold-compliance-icon,
    .policy-compliance {
        display: inline-flex;
        padding-right: 6px;
        margin: auto 0;
    }

    .cx-report .results-report .threshold-compliance-text {
        color: #21bf3f;
    }

    .cx-report .results-report .osa-libraries {
        text-align: left;
        margin-right: 35px;
        background-color: #f2f2f2;
        width: 138px;
        padding: 16px;
        padding-bottom: 22px;
    }

    .cx-report .results-report .osa-libraries-title {
        font-size: 15px;
        font-weight: 600;
        padding-bottom: 10px;
    }

    .cx-report .results-report .libraries-vulnerable {
        overflow: hidden;
    }

    .cx-report .results-report .libraries-vulnerable-number {
        font-size: 18px;
    }

    .cx-report .results-report .libraries-vulnerable-text {
        font-size: 12px;
        line-height: 15px;
    }

    .cx-report .results-report .libraries-icon-number {
        display: flex;
        margin-bottom: 5px;
        margin-top: 10px;
    }

    .cx-report .results-report .osa-results {
        display: flex;
    }

    .cx-report .results-report .threshold-line {
        width: 20px;
        border-bottom: dashed white 1px;
        padding-right: 3px;
    }

    .cx-report .results-report .chart-large .threshold-line {
        width: 53px;
    }

    .cx-report .results-report .threshold-tooltip {
        display: flex;
        transform: translate(0, 50%);
        font-size: 14px;
        align-items: center;
        color: #373050;
        background-color: white;
        text-align: center;
        padding: 6px 8px 4px;
        margin-left: 4px;
        border-radius: 2px;
        border: 1px solid #979797;
    }

    .cx-report .results-report .threshold-tooltip .tooltip-number {
        padding-right: 4px;
        padding-left: 6px;
        line-height: normal;
    }

    .cx-report .results-report .threshold-tooltip::before {
        content: "";
        position: absolute;
        width: 6px;
        height: 6px;
        top: 50%;
        transform: translate(15%, -50%) rotate(45deg);
        left: -5px;
        border-left: 1px solid #979797;
        border-bottom: 1px solid #979797;
        background-color: white;
    }

    .cx-report .results-report .tooltip-container {
        position: absolute;
        display: inline-flex;
    }

    .cx-report .full-results-section {
        border-top: solid 2px #373050;
    }

    .cx-report .summary-table-row {
        white-space: nowrap;
        display: inline-flex;
        width: 100%;
    }

    .cx-report .summary-table-row.cxosa,
    .cx-report .summary-table-row.cxsast {
        display: inline-flex;
    }

    .cx-report table {
        border-collapse: collapse;
    }

    .cx-report table td,
    .cx-report table th {
        border: solid transparent;
    }

    .cx-report .top-padding td {
        padding-top: 59px;
    }

    .cx-report td {
        border-color: transparent;
    }

    .cx-report .title-column,
    .cx-report .main-column {
        padding: 59px 0;
        line-height: 25px;
        display: inline-block;
    }

    .cx-report .main-column {
        width: 77%;
        /*max-width: 709px;*/
        min-width: 500px;
    }

    .cx-report .title-column {
        margin-right: 4%;
        vertical-align: top;
        height: 100%;
        width: 20%;
        color: #373050;
        max-width: 250px;
        min-width: 155px;
    }

    .cx-report .summary-title {
        border-bottom: solid #979797 1px;
        color: white;
        background-color: #373050;
        line-height: 28px;
        padding: 5px 13px;
    }

    .cx-report .sum1 {
        font-size: 24px;
        font-weight: 500;
    }

    .cx-report .sum2 {
        font-size: 16px;
    }

    .cx-report .sum-line1 {
        width: 100%;
        height: 1px;
        background-color: #979797;
    }

    .cx-report .graph-title {
        font-weight: 500;
        font-size: 16px;
        display: table-cell;
        vertical-align: middle;
        /*width: 75%;*/
    }

    .cx-report .graph {
        position: relative;
        text-align: left;
        line-height: 25px;
    }

    .cx-report .graph-top {
        display: table;
        vertical-align: middle;
    }

    .cx-report .detailed-report {
        margin-top: 12px;
        display: block;
        /*padding: 0 11px;*/
    }

    .cx-report .detailed-report-ttl {
        font-size: 14px;
    }

    .cx-report .download-detailed-report {
        color: #9b9b9b;
        font-size: 14px;
    }

    .cx-report .download-icon,
    .cx-report .link-text {
        display: inline-flex;
    }

    .cx-report .download-icon {
        margin-right: 9px;
    }

    .cx-report .download-icon > svg {
        vertical-align: sub;
    }

    .cx-report .download-cx-report {
        font-size: 16px;
        padding-bottom: 5px;
    }

    .cx-report .html-report.download-icon {
        margin-right: 6px;
    }

    .cx-report .pdf-report.download-icon {
        margin-right: 6px;
        border-left: 1px solid #d5d5d5;
    }

    .cx-report .summary-section .html-report {
        margin-right: 10px;
    }

    .cx-report .link-to-result a {
        display: inline-flex;
        align-items: center;
    }

    .cx-report .title-links a {
        color: #4A90E2;
        text-decoration: none;
    }

    .cx-report .report-link {
        line-height: 28px;
    }

    .cx-report .full-start-end {
        display: inline-flex;
        text-align: left;
        width: 100%;
        font-size: 14px;
        color: #525055;
        padding-top: 6px;
    }

    .cx-report .full-start-end-icon {
        margin: 4px 7px 0 0;
    }

    .cx-report .full-start,
    .cx-report .full-end,
    .cx-report .full-files,
    .cx-report .full-loc {
        display: inline-flex;
        line-height: 18px;
        margin-right: 63px;
    }

    .cx-report .full-loc {
        display: inline-block;
    }

    .cx-report .full-loc > div {
        display: inline-block;
    }

    .cx-report .osa-downloads a,
    .cx-report .sast-downloads a {
        color: #4A90E2;
        text-decoration: none;
    }

    .cx-report .full-downloads {
        white-space: normal;
    }

    .cx-report .full-severity-title {
        display: flex;
        text-align: left;
        margin-top: 45px;
        margin-bottom: 18px;
        font-size: 16px;
    }

    .cx-report .full-severity-title .severity-title-name {
        display: inline-block;
        font-size: 16px;
        margin: 0 10px;
    }

    .cx-report .full-severity-title .severity-count {
        display: inline-block;
    }

    .cx-report .full-severity-title .severity-icon {
        align-items: center;
        display: inline-flex;
    }

    .cx-report table.cve-table {
        width: 100%;
        box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.15);
    }

    .cx-report table.cve-table th,
    .cx-report table.cve-table td {
        border: 1px solid transparent;
        text-align: left;
    }

    .cx-report table.cve-table th {
        border-bottom: 1px solid #d5d5d5;
        font-size: 14px;
        color: #9b9b9b;
        font-weight: normal;
        padding: 2px 11px;
    }

    .cx-report table.cve-table td {
        font-weight: 500;
        font-size: 16px;
        color: #373050;
        border-bottom: 1px solid #d5d5d5;
        padding: 11px;
    }

    .cx-report table.cve-table td.sast-cve-table-high,
    .cx-report table.cve-table td.sast-cve-table-medium,
    .cx-report table.cve-table td.sast-cve-table-low {
        max-width: 19px;
    }

    .cx-report table.cve-table.sast-cve-table td:last-child,
    .cx-report table.cve-table.sast-cve-table th:last-child {
        padding-right: 20px;
        text-align: right;
    }

    .cx-report table.cve-table.osa-cve-table td:last-child,
    .cx-report table.cve-table.osa-cve-table th:last-child {
        width: 32%;
    }

    .cx-report .error-msg {
        display: block;
        padding: 4% 0;
    }

    .cx-report .chart-large .no-scan-message-container {
        height: 372px;
    }

    .cx-report .no-scan-message-container {
        height: 407px;
        width: 100%;
        background-color: #F2F2F2;
        z-index: 1;
        margin-top: 17px;
        margin-bottom: 21px;
    }

    .cx-report .no-scan-message-icon {
        padding: 27px;
    }

    .cx-report .no-scan-message {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        color: #382F50;
        font-size: 15px;
        text-align: center;
        width: 89%;
    }


    .scan-status {
        display: flex;
        margin-top: 26px;
        min-height: 45px;
    }

    .scan-status .content-scan-status {
        color: black;
        font-family: Roboto, sans-serif;
        font-size: 12px;
        line-height: 16px;
    }

    .scan-status .content-scan-status ul {
        margin-top: -3px;
        margin-left: -7px;
    }

    .scan-status .content-scan-status li {
        margin-top: 6px;
        margin-bottom: 6px;
    }

    .scan-status.success {
        border: 1px solid #38d87d;
    }

    .scan-status.failure {
        border: 1px solid #DD3D56;
    }

    .scan-status .indicator-scan-status {
        width: 50px;
        padding-top: 10px;
    }

    .scan-status .indicator-scan-status.success {
        background-color: #38d87d;
    }

    .scan-status .indicator-scan-status.failure {
        background-color: #DD3D56;
    }

    .scan-status .indicator-scan-status .icon-scan-status {
        margin-top: 5px;
        text-align: center;
        border: 1px solid #ffffff;
        padding-left: 5px;
    }

    .scan-status .indicator-scan-status.success .icon-scan-status {
        border: 1px solid #38d87d;
    }

    .scan-status .indicator-scan-status.failure .icon-scan-status {
        border: 1px solid #DD3D56;
    }

    .scan-status .title-scan-status {
        font-size: 12px;
        font-weight: bold;
        padding-left: 16px;
        padding-top: 10px;
    }

    .scan-status .content-scan-status .title-scan-status.failure {
        padding-top: 7px;
        color: #DD3D56;
        padding-bottom: 3px;
    }

    .scan-status .content-scan-status .title-scan-status.success {
        color: #38d87d;
    }

    #ruleName {
        max-width: 100px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

</style>

<#macro thresholdTooltip threshold count>
    <#if count gt threshold>
    <div class="tooltip-container" style="bottom:calc( ${threshold * 100 / count}% - 1px)">
        <div class="threshold-line">
        </div>
        <div class="threshold-tooltip">
            <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="12px"
                 height="12px"
                 viewBox="0 0 12 12" version="1.1">
                <defs/>
                <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                    <g id="Icons" transform="translate(-87.000000, -243.000000)">
                        <g id="threshhold-icon-red" transform="translate(87.000000, 243.000000)">
                            <g>
                                <path d="M8.0904685,3 L7.0904685,3 L7.0904685,5 L8.0904685,5 L8.0904685,11 L3.0904685,11 L3.0904685,0 L8.0904685,0 L8.0904685,3 Z M3.0904685,3 L3.0904685,5 L5.0904685,5 L5.0904685,3 L3.0904685,3 Z M5.0904685,3 L5.0904685,5 L7.0904685,5 L7.0904685,3 L5.0904685,3 Z"
                                      id="Combined-Shape" fill="#DA2945"/>
                                <path d="M10.5904685,11.5 L0.590468498,11.5" id="Line" stroke="#DA2945"
                                      stroke-linecap="square"/>
                            </g>
                        </g>
                    </g>
                </g>
            </svg>
            <div class="tooltip-number">${threshold}</div>
        </div>
    </div>
    </#if>
</#macro>


<div id="cx-report" class="cx-report">
    <div class="report-title">
        <div class="cx-report-title">Checkmarx Report</div>


    <#if buildFailed>
        <div class='scan-status failure'>
            <div class='indicator-scan-status failure'>
                <div class='icon-scan-status'>
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="22px"
                         height="22px" viewBox="0 0 22 22" version="1.1">
                        <!-- Generator: Sketch 50.2 (55047) - http://www.bohemiancoding.com/sketch -->
                        <title>error</title>
                        <desc>Created with Sketch.</desc>
                        <defs/>
                        <g id="Policy-mgmt" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                            <g id="Jenkins-eport-policy-stat" transform="translate(-90.000000, -32.000000)"
                               fill="#FFFFFF">
                                <g id="Group-13" transform="translate(79.000000, 24.000000)">
                                    <g id="Group-15-Copy">
                                        <g id="Group-14" transform="translate(11.000000, 8.000000)">
                                            <g id="error">
                                                <path d="M8.88864088,11.0488591 L5.97182541,13.9656746 L7.91636906,15.9102182 L10.8331845,12.9934028 L13.75,15.9102182 L15.6945436,13.9656746 L12.7777282,11.0488591 L15.6945436,8.13204365 L13.75,6.1875 L10.8331845,9.10431547 L7.91636906,6.1875 L5.97182541,8.13204365 L8.88864088,11.0488591 Z M11,22 C4.92486775,22 0,17.0751322 0,11 C0,4.92486775 4.92486775,0 11,0 C17.0751322,0 22,4.92486775 22,11 C22,17.0751322 17.0751322,22 11,22 Z"
                                                      id="Combined-Shape"/>
                                            </g>
                                        </g>
                                    </g>
                                </g>
                            </g>
                        </g>
                    </svg>
                </div>
            </div>
            <div class='content-scan-status'>
                <p class="title-scan-status failure">
                    Checkmarx scan found the following issues:
                </p>
                <ul>
                    <#if config.isSastEnabled() && !sast.sastResultsReady>
                        <li>SAST Scan Failed</li>
                    </#if>
                    <#if config.isOsaEnabled() && !dependencyResult.resultReady>
                        <li>OSA Scan Failed</li>
                    </#if>
                    <#if policyViolated>
                        <li>${policyViolatedCount} ${policyLabel}  Violated</li>
                    </#if>
                    <#if config.isSastEnabled() && sast.sastResultsReady && (sastThresholdExceeded || sastNewResultsExceeded) && (config.isOsaEnabled() || config.isAstScaEnabled()) && dependencyResult.resultReady && dependencyThresholdExceeded>
                        <li>Exceeded CxSAST and CxOSA/CxSCA Vulnerability Thresholds</li>
                    <#elseif config.isSastEnabled() && sast.sastResultsReady && (sastThresholdExceeded || sastNewResultsExceeded)>
                        <li>Exceeded CxSAST Vulnerability Threshold</li>
                    <#elseif config.isOsaEnabled() && dependencyResult.resultReady && dependencyThresholdExceeded>
                        <li>Exceeded CxOSA Vulnerability Threshold</li>
                    <#elseif config.isAstScaEnabled() && dependencyResult.resultReady && dependencyThresholdExceeded>
                        <li>Exceeded CxSCA Vulnerability Threshold</li>
                    <#else>
                        <li>CxScan Failed</li>
                    </#if>
                </ul>
            </div>
        </div>
    <#else>
        <div class='scan-status success'>
            <div class='indicator-scan-status success'>
                <div class='icon-scan-status'>
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="24px"
                         height="24px" viewBox="0 0 24 24" version="1.1">
                        <!-- Generator: Sketch 50.2 (55047) - http://www.bohemiancoding.com/sketch -->
                        <title>OK</title>
                        <desc>Created with Sketch.</desc>
                        <defs/>
                        <g id="Policy-mgmt" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                            <g id="Jenkins-eport-policy-stat" transform="translate(-89.000000, -130.000000)">
                                <g id="Group-13-Copy" transform="translate(79.000000, 124.000000)">
                                    <g id="Group-15-Copy">
                                        <g id="Group-14" transform="translate(10.000000, 6.000000)">
                                            <g id="disconected">
                                                <g id="OK">
                                                    <circle id="Oval" fill="#FFFFFF" cx="12" cy="12" r="12"/>
                                                    <path d="M9.45495129,11.7049513 L18.4549513,11.7049513 L18.4549513,14.7049513 L6.45495129,14.7049513 L6.45495129,11.7049513 L6.45495129,7.20495129 L9.45495129,7.20495129 L9.45495129,11.7049513 Z"
                                                          id="Combined-Shape" fill="#38D87D"
                                                          transform="translate(12.454951, 10.954951) rotate(-50.000000) translate(-12.454951, -10.954951) "/>
                                                </g>
                                            </g>
                                        </g>
                                    </g>
                                </g>
                            </g>
                        </g>
                    </svg>
                </div>
            </div>
            <div class='content-scan-status'>
                <p class="title-scan-status success">
                    Checkmarx Scan Passed
                </p>
            </div>
        </div>

    </#if>


    </div>
    <div id="results-report" class="results-report">

        <div class="summary-section">
            <div id="summary-results" class="summary-results">

            <#if config.isSastEnabled()>
                <div class="sast-summary <#if !(config.isOsaEnabled() || config.isAstScaEnabled()) >chart-large</#if>" id="sast-summary">
                    <div class="summary-report-title sast">
                        <div class="summary-title-text sast">CxSAST Vulnerabilities Status</div>
                        <#if sast.sastResultsReady>
                            <div id="sast-title-links" class="title-links">
                                <div class="link-to-result summary-link">
                                    <a href="${sast.sastScanLink}" class="html-report" id="sast-summary-html-link">
                                        <div class="results-link-icon link-icon">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="14"
                                                 viewBox="0 0 12 14">
                                                viewBox="0 0 12 14">
                                                <g fill="none" fill-rule="evenodd">
                                                    <circle stroke="#4A90E2" stroke-width="2" cx="5" cy="5"
                                                            r="4"></circle>
                                                    <path fill="#4A90E2"
                                                          d="M6.366 8.366l1.732-1 3.268 5.66-1.732 1z"></path>
                                                </g>
                                            </svg>
                                        </div>
                                        <div class="link-text">Results</div>
                                    </a>
                                </div>

                                <#if sast.sastPDFLink??>
                                    <div id="sast-summary-pdf-link" class="link-to-result">
                                        <a class="pdf-report" href="${sast.sastPDFLink}">
                                            <div class="link-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="13" height="16"
                                                     viewBox="0 0 13 16"><title>PDF icon</title>
                                                    <g fill="none" fill-rule="evenodd">
                                                        <path d="M8.55 0H8v5h5V4L9.097 0H8.55z" fill="#4A90E2"/>
                                                        <path d="M.5.5h8.387L12.5 4.204V15.5H.5V.5z" stroke="#4A90E2"/>
                                                        <text font-family="Roboto-Black, Roboto" font-size="5"
                                                              font-weight="700" fill="#4A90E2">
                                                            <tspan x="1.889" y="11">PDF</tspan>
                                                        </text>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="link-text">
                                                PDF Report
                                            </div>
                                        </a>
                                    </div>
                                </#if>
                            </div>
                        <#else>
                            <div id="no-scan-message-sast-failed" class="no-scan-message-container sast-scan-failed"
                                 style="display: inline-block">
                                <div class="no-scan-message">
                                    <div class="no-scan-message-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg"
                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="48px" height="37px"
                                             viewBox="0 0 48 37"
                                             version="1.1">
                                            <!-- Generator: Sketch 45.2 (43514) - http:/*www.bohemiancoding.com/sketch -->
                                            */ <title>scan_failed</title>
                                            <desc>Created with Sketch.</desc>
                                            <defs/>
                                            <g id="Page-1" stroke="none" stroke-width="1" fill="none"
                                               fill-rule="evenodd">
                                                <g id="Jenkins-OSA-empty"
                                                   transform="translate(-936.000000, -335.000000)">
                                                    <g id="Group-2" transform="translate(683.000000, 179.000000)">
                                                        <g id="Group" transform="translate(81.000000, 155.202941)">
                                                            <g id="Group-3" transform="translate(171.000000, 0.000000)">
                                                                <g transform="translate(2.000606, 1.714748)">
                                                                    <path d="M24.3114123,0.619831205 L44.98533,32.7440758 L44.98533,32.7440758 C45.2842135,33.208497 45.1500186,33.8272778 44.6855974,34.1261613 C44.5242092,34.2300245 44.336342,34.2852522 44.1444209,34.2852522 L1.8977196,34.2852522 L1.8977196,34.2852522 C1.34543485,34.2852522 0.897719599,33.837537 0.897719599,33.2852522 C0.897719599,33.0866799 0.956838199,32.8926033 1.0675421,32.7277531 L22.6403257,0.60350849 L22.6403257,0.60350849 C22.948224,0.145014117 23.569508,0.0229318695 24.0280024,0.330830157 C24.1411852,0.406837189 24.2376306,0.50518529 24.3114123,0.619831205 Z"
                                                                          id="Triangle" stroke="#373050"
                                                                          stroke-width="2"/>
                                                                    <rect id="Rectangle-1" fill="#373050" x="21.9993936"
                                                                          y="11.2852522" width="3" height="13"
                                                                          rx="1.5"/>
                                                                    <path d="M23.4993936,26.2852522 L23.4993936,26.2852522 L23.4993936,26.2852522 C24.3278207,26.2852522 24.9993936,26.9568251 24.9993936,27.7852522 L24.9993936,27.7852522 L24.9993936,27.7852522 C24.9993936,28.6136794 24.3278207,29.2852522 23.4993936,29.2852522 L23.4993936,29.2852522 L23.4993936,29.2852522 C22.6709664,29.2852522 21.9993936,28.6136794 21.9993936,27.7852522 L21.9993936,27.7852522 L21.9993936,27.7852522 C21.9993936,26.9568251 22.6709664,26.2852522 23.4993936,26.2852522 Z"
                                                                          id="Rectangle-1-Copy" fill="#373050"/>
                                                                </g>
                                                            </g>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="no-scan-message-text">SAST scan failed</div>
                                </div>
                            </div>
                        </#if>
                    </div>
                    <#if sast.sastResultsReady>
                        <!--sast-chart-->
                        <div class="summary-chart" id="sast-results">
                            <div class="top-of-chart">
                                <#if sast.hasNewResults()>
                                    <div id="sast-legend-container" class="chart-legend-container">
                                        <div class="chart-legend">
                                            <div class="legend-item recurrent-legend">
                                                <div class="legend-color-box recurrent-legend-color"></div>
                                                <div class="legend-text recurrent-legend-text">Recurrent</div>
                                            </div>
                                            <div class="legend-item new-legend">
                                                <div class="legend-color-box new-legend-color"></div>
                                                <div class="legend-text new-legend-text">New</div>
                                            </div>
                                        </div>
                                    </div>
                                </#if>
                            </div>

                            <ul class="chart">
                                <!--sast-high-->
                                <li>
                                        <span class="bar-1" id="bar-high" style="height: ${sastHighTotalHeight}px">
                                            <div id="tooltip-high">
                                                <#if config.sastThresholdsEnabled && config.sastHighThreshold??>
                                                    <@thresholdTooltip threshold=config.sastHighThreshold count=sast.high/>
                                                </#if>
                                            </div>
                                            <div id="high-new-scans" class="new-scans"
                                                 style="height: ${sastHighNewHeight}px"></div>
                                            <div id="high-recurrent-scans" class="recurrent-scans"
                                                 style="height: ${sastHighRecurrentHeight}px"></div>
                                        </span>

                                    <div class="bar-title-wrapper">
                                        <div class="bar-title-container">
                                            <div class="bar-title-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                     xmlns:xlink="http://www.w3.org/1999/xlink"
                                                     width="16px" height="19px" viewBox="0 0 16 19" version="1.1">
                                                    <!-- Generator: Sketch 43.2 (39069) - http://www.bohemiancoding.com/sketch -->
                                                    <title>Med</title>
                                                    <desc>Created with Sketch.</desc>
                                                    <defs>
                                                        <path d="M1,1 L8,0 L15,1 C15,1 16,4.01515152 16,7 C16,13.0151515 10.6766131,18.2701936 10.6766131,18.2701936 C10.30293,18.6732545 9.55664682,19 8.9906311,19 L7.0093689,19 C6.45190985,19 5.70245907,18.6673641 5.33497024,18.2411641 C5.33497024,18.2411641 3.70193273e-12,12.5151515 3.63797881e-12,8 C7.03437308e-13,4.82765152 1,1 1,1 Z"
                                                              id="path-1"/>
                                                        <path d="M1,1 L8,0 L15,1 C15,1 16,4.01515152 16,7 C16,13.0151515 10.6766131,18.2701936 10.6766131,18.2701936 C10.30293,18.6732545 9.55664682,19 8.9906311,19 L7.0093689,19 C6.45190985,19 5.70245907,18.6673641 5.33497024,18.2411641 C5.33497024,18.2411641 3.70193273e-12,12.5151515 3.63797881e-12,8 C7.03437308e-13,4.82765152 1,1 1,1 Z"
                                                              id="path-3"/>
                                                    </defs>
                                                    <g id="Page-1" stroke="none" stroke-width="1" fill="none"
                                                       fill-rule="evenodd">
                                                        <g id="Icons" transform="translate(-47.000000, -88.000000)">
                                                            <g id="High" transform="translate(47.000000, 88.000000)">
                                                                <g id="Vonerability-High">
                                                                    <mask id="mask-2" fill="white">
                                                                        <use xlink:href="#path-1"/>
                                                                    </mask>
                                                                    <g id="Rectangle-10">
                                                                        <use fill="#D82D49" fill-rule="evenodd"
                                                                             xlink:href="#path-1"/>
                                                                        <path stroke="#BB1A34" stroke-width="1"
                                                                              d="M1.4041953,1.44733409 L8,0.505076272 L14.6160396,1.45022478 C14.6341112,1.51124347 14.6539641,1.5795116 14.6753578,1.65465958 C14.7899552,2.05719756 14.9047222,2.50600605 15.0118679,2.98897331 C15.3098751,4.33226343 15.4915175,5.67204692 15.4997158,6.91419406 C15.4999523,6.95710967 15.4999523,6.95710967 15.5,7 C15.5,9.52090451 14.5340777,12.111589 12.9179883,14.6199787 C12.3484584,15.5039663 11.7377754,16.313821 11.1275564,17.0311249 C10.9144997,17.2815702 10.7170402,17.5022391 10.5403911,17.6908777 C10.4358029,17.8025645 10.3623853,17.8778048 10.3253512,17.9143634 C10.0291161,18.2331673 9.41484636,18.5 8.9906311,18.5 L7.0093689,18.5 C6.59080843,18.5 5.98194778,18.2258269 5.71364227,17.9146561 C5.66213668,17.8588317 5.58703389,17.7761053 5.4807125,17.6555634 C5.30200204,17.4529504 5.10247221,17.2193106 4.88735491,16.9580823 C4.27213719,16.2109907 3.656779,15.394289 3.08320773,14.5359605 C2.09721248,13.0604546 1.34127053,11.6205479 0.906388115,10.2835472 C0.639104683,9.46181216 0.5,8.69692293 0.5,8 C0.5,7.56658708 0.519280284,7.10494686 0.556403808,6.61890492 C0.63408435,5.60186781 0.786470164,4.51217341 0.991682584,3.40118912 C1.09968656,2.81647439 1.21542088,2.26333889 1.3310756,1.7595034 C1.35796875,1.64234673 1.3824953,1.53794489 1.4041953,1.44733409 Z"/>
                                                                    </g>
                                                                    <rect id="Rectangle-22" fill="#BB1A34"
                                                                          mask="url(#mask-2)" x="8"
                                                                          y="0" width="8" height="20"/>
                                                                    <mask id="mask-4" fill="white">
                                                                        <use xlink:href="#path-3"/>
                                                                    </mask>
                                                                    <path stroke="#BB1A34"
                                                                          d="M1.4041953,1.44733409 L8,0.505076272 L14.6160396,1.45022478 C14.6341112,1.51124347 14.6539641,1.5795116 14.6753578,1.65465958 C14.7899552,2.05719756 14.9047222,2.50600605 15.0118679,2.98897331 C15.3098751,4.33226343 15.4915175,5.67204692 15.4997158,6.91419406 C15.4999523,6.95710967 15.4999523,6.95710967 15.5,7 C15.5,9.52090451 14.5340777,12.111589 12.9179883,14.6199787 C12.3484584,15.5039663 11.7377754,16.313821 11.1275564,17.0311249 C10.9144997,17.2815702 10.7170402,17.5022391 10.5403911,17.6908777 C10.4358029,17.8025645 10.3623853,17.8778048 10.3253512,17.9143634 C10.0291161,18.2331673 9.41484636,18.5 8.9906311,18.5 L7.0093689,18.5 C6.59080843,18.5 5.98194778,18.2258269 5.71364227,17.9146561 C5.66213668,17.8588317 5.58703389,17.7761053 5.4807125,17.6555634 C5.30200204,17.4529504 5.10247221,17.2193106 4.88735491,16.9580823 C4.27213719,16.2109907 3.656779,15.394289 3.08320773,14.5359605 C2.09721248,13.0604546 1.34127053,11.6205479 0.906388115,10.2835472 C0.639104683,9.46181216 0.5,8.69692293 0.5,8 C0.5,7.56658708 0.519280284,7.10494686 0.556403808,6.61890492 C0.63408435,5.60186781 0.786470164,4.51217341 0.991682584,3.40118912 C1.09968656,2.81647439 1.21542088,2.26333889 1.3310756,1.7595034 C1.35796875,1.64234673 1.3824953,1.53794489 1.4041953,1.44733409 Z"/>
                                                                    <polygon id="H" fill="#FFFFFF" mask="url(#mask-4)"
                                                                             points="5 12 7 12 7 9.5 9 9.5 9 12 11 12 11 5 9 5 9 7.5 7 7.5 7 5 5 5"/>
                                                                </g>
                                                            </g>
                                                        </g>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="bar-title">High -</div>
                                            <div class="bar-count" id="bar-count-high">${sast.high}</div>
                                        </div>
                                        <#if sast.hasNewResults()>
                                            <div id="new-bar-count-high" class="new-bar-title-container">${sast.newHigh}
                                                New
                                            </div>
                                        </#if>
                                    </div>
                                </li>

                                <!--sast-medium-->
                                <li>
                                        <span class="bar-2" id="bar-med" style="height: ${sastMediumTotalHeight}px">
                                                <div id="tooltip-med">
                                                    <#if config.sastThresholdsEnabled && config.sastMediumThreshold??>
                                                        <@thresholdTooltip threshold=config.sastMediumThreshold count=sast.medium/>
                                                    </#if>
                                                </div>
                                                <div id="med-new-scans" class="new-scans"
                                                     style="height: ${sastMediumNewHeight}px"></div>
                                                <div id="med-recurrent-scans" class="recurrent-scans"
                                                     style="height: ${sastMediumRecurrentHeight}px"></div>
                                        </span>
                                    <div class="bar-title-wrapper">
                                        <div class="bar-title-container">
                                            <div class="bar-title-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                     xmlns:xlink="http://www.w3.org/1999/xlink"
                                                     width="16" height="20" viewBox="0 0 16 20"><title>Low</title>
                                                    <defs>
                                                        <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                              id="a"/>
                                                        <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                              id="c"/>
                                                    </defs>
                                                    <g fill="none" fill-rule="evenodd">
                                                        <mask id="b" fill="#fff">
                                                            <use xlink:href="#a"/>
                                                        </mask>
                                                        <use fill="#FFAC00" xlink:href="#a"/>
                                                        <path stroke="#E49B16"
                                                              d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.973 5.404-2.6 8.06-.572.934-1.186 1.79-1.8 2.55-.214.264-.413.498-.59.698-.106.118-.18.198-.217.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.215-.38-.463-.595-.74-.62-.79-1.237-1.653-1.814-2.56C2.12 13.79 1.363 12.28.923 10.877.644 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.615.224-1.198.34-1.73l.077-.343z"/>
                                                        <path fill="#D79201" mask="url(#b)" d="M8 0h8v20H8z"/>
                                                        <mask id="d" fill="#fff">
                                                            <use xlink:href="#c"/>
                                                        </mask>
                                                        <path stroke="#D49100"
                                                              d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.973 5.404-2.6 8.06-.572.934-1.186 1.79-1.8 2.55-.214.264-.413.498-.59.698-.106.118-.18.198-.217.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.215-.38-.463-.595-.74-.62-.79-1.237-1.653-1.814-2.56C2.12 13.79 1.363 12.28.923 10.877.644 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.615.224-1.198.34-1.73l.077-.343z"/>
                                                        <path fill="#472F00" mask="url(#d)"
                                                              d="M4.28 12.632h1.9v-4.21l1.78 2.862H8L9.79 8.4v4.232h1.93v-7.37H9.67L8 8.117 6.33 5.263H4.28"/>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="bar-title">Medium -</div>
                                            <div class="bar-count" id="bar-count-med">${sast.medium}</div>
                                        </div>
                                        <#if sast.hasNewResults()>
                                            <div id="new-bar-count-medium"
                                                 class="new-bar-title-container">${sast.newMedium} New
                                            </div>
                                        </#if>
                                    </div>
                                </li>

                                <!--sast-low-->
                                <li>
                                        <span class="bar-3" id="bar-low" style="height: ${sastLowTotalHeight}px">
                                            <div id="tooltip-low">
                                                <#if config.sastThresholdsEnabled && config.sastLowThreshold??>
                                                    <@thresholdTooltip threshold=config.sastLowThreshold count=sast.low/>
                                                </#if>
                                            </div>
                                            <div id="low-new-scans" class="new-scans"
                                                 style="height: ${sastLowNewHeight}px"></div>
                                            <div id="low-recurrent-scans" class="recurrent-scans"
                                                 style="height: ${sastLowRecurrentHeight}px"></div>
                                        </span>
                                    <div class="bar-title-wrapper">
                                        <div class="bar-title-container">
                                            <div class="bar-title-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                     xmlns:xlink="http://www.w3.org/1999/xlink"
                                                     width="16" height="19" viewBox="0 0 16 19">
                                                    <title>Low</title>
                                                    <defs>
                                                        <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                              id="sast-low-path-1"/>
                                                        <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                              id="sast-low-path-2"/>
                                                    </defs>
                                                    <g fill="none" fill-rule="evenodd">
                                                        <path d="M7.96 17.32L8 .015l-6.5 1s-.96 4.5-.96 8.75c1.272 4.602 5.968 9.25 5.968 9.25h.163l1.29-1.695z"
                                                              fill="#EDEFF5"/>
                                                        <mask id="sast-low-mask-1" fill="#fff">
                                                            <use xlink:href="#sast-low-path-1"/>
                                                        </mask>
                                                        <use fill="#FFEB3B" xlink:href="#sast-low-path-1"/>
                                                        <path stroke="#E4D200"
                                                              d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.342.48 2.682.488 3.924V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38C.634 5.6.786 4.51.992 3.4c.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                        <path fill="#DDCE00" mask="url(#sast-low-mask-1)"
                                                              d="M8-8h10v32H8z"/>
                                                        <mask id="sast-low-mask-2" fill="#fff">
                                                            <use xlink:href="#sast-low-path-2"/>
                                                        </mask>
                                                        <path stroke="#E4D200"
                                                              d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.342.48 2.682.488 3.924V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38C.634 5.6.786 4.51.992 3.4c.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                        <path fill="#605900" mask="url(#sast-low-mask-2)"
                                                              d="M5.54 12h5.33v-1.7H7.48V5H5.54"/>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="bar-title">Low -</div>
                                            <div class="bar-count" id="bar-count-low">${sast.low}</div>
                                        </div>
                                        <#if sast.hasNewResults()>
                                            <div id="new-bar-count-low" class="new-bar-title-container">${sast.newLow}
                                                New
                                            </div>
                                        </#if>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </#if>
                </div>
            </#if>

                <#if config.isOsaEnabled()|| config.isAstScaEnabled() >
        <div class="osa-summary <#if !config.isSastEnabled()>sast-summary chart-large</#if>" id="osa-summary">
            <div class="summary-report-title osa">
                <#if dependencyResult.scannerType=="AST_SCA">
                <div class="summary-title-text osa">CxSCA Vulnerabilities & Libraries</div>
                <#else>
                <div class="summary-title-text osa">Cx${dependencyResult.scannerType} Vulnerabilities & Libraries</div>
                </#if>
                <#if dependencyResult.resultReady>
                    <div id="osa-title-links" class="title-links">
                        <div class="link-to-result summary-link">
                            <a href="${dependencyResult.summaryLink}" class="html-report"
                               id="osa-summary-html-link">
                                <div class="results-link-icon link-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="14"
                                         viewBox="0 0 12 14">
                                        <title>analize</title>
                                        <g fill="none" fill-rule="evenodd">
                                            <circle stroke="#4A90E2" stroke-width="2" cx="5" cy="5" r="4"/>
                                            <path fill="#4A90E2" d="M6.366 8.366l1.732-1 3.268 5.66-1.732 1z"/>
                                        </g>
                                    </svg>
                                </div>
                                <div class="link-text">Results</div>
                            </a>
                        </div>
                    </div>
                <#else>
                    <div id="no-scan-message-osa-failed" class="no-scan-message-container osa-scan-failed">
                        <div class="no-scan-message">
                            <div class="no-scan-message-icon">
                                <svg xmlns="http://www.w3.org/2000/svg"
                                     xmlns:xlink="http://www.w3.org/1999/xlink"
                                     width="48px" height="37px" viewBox="0 0 48 37"
                                     version="1.1">
                                    <!-- Generator: Sketch 45.2 (43514) - http:/*www.bohemiancoding.com/sketch -->
                                    */
                                    <title>scan_failed</title>
                                    <desc>Created with Sketch.</desc>
                                    <defs/>
                                    <g id="Page-1" stroke="none" stroke-width="1" fill="none"
                                       fill-rule="evenodd">
                                        <g id="Jenkins-OSA-empty"
                                           transform="translate(-936.000000, -335.000000)">
                                            <g id="Group-2" transform="translate(683.000000, 179.000000)">
                                                <g id="Group" transform="translate(81.000000, 155.202941)">
                                                    <g id="Group-3" transform="translate(171.000000, 0.000000)">
                                                        <g transform="translate(2.000606, 1.714748)">
                                                            <path d="M24.3114123,0.619831205 L44.98533,32.7440758 L44.98533,32.7440758 C45.2842135,33.208497 45.1500186,33.8272778 44.6855974,34.1261613 C44.5242092,34.2300245 44.336342,34.2852522 44.1444209,34.2852522 L1.8977196,34.2852522 L1.8977196,34.2852522 C1.34543485,34.2852522 0.897719599,33.837537 0.897719599,33.2852522 C0.897719599,33.0866799 0.956838199,32.8926033 1.0675421,32.7277531 L22.6403257,0.60350849 L22.6403257,0.60350849 C22.948224,0.145014117 23.569508,0.0229318695 24.0280024,0.330830157 C24.1411852,0.406837189 24.2376306,0.50518529 24.3114123,0.619831205 Z"
                                                                  id="Triangle" stroke="#373050"
                                                                  stroke-width="2"/>
                                                            <rect id="Rectangle-6" fill="#373050" x="21.9993936"
                                                                  y="11.2852522" width="3" height="13"
                                                                  rx="1.5"/>
                                                            <path d="M23.4993936,26.2852522 L23.4993936,26.2852522 L23.4993936,26.2852522 C24.3278207,26.2852522 24.9993936,26.9568251 24.9993936,27.7852522 L24.9993936,27.7852522 L24.9993936,27.7852522 C24.9993936,28.6136794 24.3278207,29.2852522 23.4993936,29.2852522 L23.4993936,29.2852522 L23.4993936,29.2852522 C22.6709664,29.2852522 21.9993936,28.6136794 21.9993936,27.7852522 L21.9993936,27.7852522 L21.9993936,27.7852522 C21.9993936,26.9568251 22.6709664,26.2852522 23.4993936,26.2852522 Z"
                                                                  id="Rectangle-6-Copy" fill="#373050"/>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </g>
                                </svg>
                            </div>
                            <div class="no-scan-message-text">OSA scan failed</div>
                        </div>
                    </div>
                </#if>
            </div>
            <#if dependencyResult.resultReady>
                <div class="osa-results" id="osa-results">
                    <!--osa-libraries-count-->
                    <div class="osa-libraries">
                        <div class="osa-libraries-title">Libraries:</div>
                        <!--osa-libs-vulnerable-->
                        <div class="libraries-vulnerable">
                            <div class="libraries-icon-number">
                                <div class="libraries-vulnerable-number"
                                     id="vulnerable-libraries">${dependencyResult.vulnerableAndOutdated}</div>
                            </div>
                            <div class="libraries-vulnerable-text">
                                Vulnerable and Outdated Libraries
                            </div>
                        </div>
                        <!--osa-libs-ok-->
                        <div class="libraries-vulnerable">
                            <div class="libraries-icon-number">
                                <div class="libraries-vulnerable-number"
                                     id="vulnerable-libraries">${dependencyResult.nonVulnerableLibraries}</div>
                            </div>
                            <div class="libraries-vulnerable-text">
                                No Known Vulnerability Libraries
                            </div>
                        </div>
                    </div>
                    <!--osa-chart-->
                    <div class="osa-chart">
                        <ul class="osa-chart chart">
                            <!--osa-high-->
                            <li>
                                            <span class="bar-1" id="osa-bar-high"
                                                  style="height: ${dependencyHighTotalHeight}px">
                                                <div id="osa-tooltip-high">
                                                    <#if config.osaThresholdsEnabled && config.osaHighThreshold??>
                                                        <@thresholdTooltip threshold=config.osaHighThreshold count=dependencyResult.highVulnerability/>
                                                    </#if>
                                                </div>
                                            </span>

                                <div class="bar-title-wrapper">
                                    <div class="bar-title-container">
                                        <div class="bar-title-icon">
                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                 xmlns:xlink="http://www.w3.org/1999/xlink" width="16px"
                                                 height="19px"
                                                 viewBox="0 0 16 19"
                                                 version="1.1">
                                                <!-- Generator: Sketch 43.2 (39069) - http://www.bohemiancoding.com/sketch -->
                                                <title>Med</title>
                                                <desc>Created with Sketch.</desc>
                                                <defs>
                                                    <path d="M1,1 L8,0 L15,1 C15,1 16,4.01515152 16,7 C16,13.0151515 10.6766131,18.2701936 10.6766131,18.2701936 C10.30293,18.6732545 9.55664682,19 8.9906311,19 L7.0093689,19 C6.45190985,19 5.70245907,18.6673641 5.33497024,18.2411641 C5.33497024,18.2411641 3.70193273e-12,12.5151515 3.63797881e-12,8 C7.03437308e-13,4.82765152 1,1 1,1 Z"
                                                          id="osa-high-path-1"/>
                                                    <path d="M1,1 L8,0 L15,1 C15,1 16,4.01515152 16,7 C16,13.0151515 10.6766131,18.2701936 10.6766131,18.2701936 C10.30293,18.6732545 9.55664682,19 8.9906311,19 L7.0093689,19 C6.45190985,19 5.70245907,18.6673641 5.33497024,18.2411641 C5.33497024,18.2411641 3.70193273e-12,12.5151515 3.63797881e-12,8 C7.03437308e-13,4.82765152 1,1 1,1 Z"
                                                          id="osa-high-path-2"/>
                                                </defs>
                                                <g id="osa-high-Page-1" stroke="none" stroke-width="1"
                                                   fill="none"
                                                   fill-rule="evenodd">
                                                    <g id="osa-high-Icons"
                                                       transform="translate(-47.000000, -88.000000)">
                                                        <g id="osa-high-High"
                                                           transform="translate(47.000000, 88.000000)">
                                                            <g id="osa-high-Vonerability">
                                                                <mask id="osa-high-mask-1" fill="white">
                                                                    <use xlink:href="#osa-high-path-1"/>
                                                                </mask>
                                                                <g id="osa-high-Rectangle-1">
                                                                    <use fill="#D82D49" fill-rule="evenodd"
                                                                         xlink:href="#osa-high-path-1"/>
                                                                    <path stroke="#BB1A34" stroke-width="1"
                                                                          d="M1.4041953,1.44733409 L8,0.505076272 L14.6160396,1.45022478 C14.6341112,1.51124347 14.6539641,1.5795116 14.6753578,1.65465958 C14.7899552,2.05719756 14.9047222,2.50600605 15.0118679,2.98897331 C15.3098751,4.33226343 15.4915175,5.67204692 15.4997158,6.91419406 C15.4999523,6.95710967 15.4999523,6.95710967 15.5,7 C15.5,9.52090451 14.5340777,12.111589 12.9179883,14.6199787 C12.3484584,15.5039663 11.7377754,16.313821 11.1275564,17.0311249 C10.9144997,17.2815702 10.7170402,17.5022391 10.5403911,17.6908777 C10.4358029,17.8025645 10.3623853,17.8778048 10.3253512,17.9143634 C10.0291161,18.2331673 9.41484636,18.5 8.9906311,18.5 L7.0093689,18.5 C6.59080843,18.5 5.98194778,18.2258269 5.71364227,17.9146561 C5.66213668,17.8588317 5.58703389,17.7761053 5.4807125,17.6555634 C5.30200204,17.4529504 5.10247221,17.2193106 4.88735491,16.9580823 C4.27213719,16.2109907 3.656779,15.394289 3.08320773,14.5359605 C2.09721248,13.0604546 1.34127053,11.6205479 0.906388115,10.2835472 C0.639104683,9.46181216 0.5,8.69692293 0.5,8 C0.5,7.56658708 0.519280284,7.10494686 0.556403808,6.61890492 C0.63408435,5.60186781 0.786470164,4.51217341 0.991682584,3.40118912 C1.09968656,2.81647439 1.21542088,2.26333889 1.3310756,1.7595034 C1.35796875,1.64234673 1.3824953,1.53794489 1.4041953,1.44733409 Z"/>
                                                                </g>
                                                                <rect id="osa-high-Rectangle-2" fill="#BB1A34"
                                                                      mask="url(#osa-high-mask-1)"
                                                                      x="8" y="0" width="8" height="20"/>
                                                                <mask id="osa-high-mask-2" fill="white">
                                                                    <use xlink:href="#osa-high-path-2"/>
                                                                </mask>
                                                                <path stroke="#BB1A34"
                                                                      d="M1.4041953,1.44733409 L8,0.505076272 L14.6160396,1.45022478 C14.6341112,1.51124347 14.6539641,1.5795116 14.6753578,1.65465958 C14.7899552,2.05719756 14.9047222,2.50600605 15.0118679,2.98897331 C15.3098751,4.33226343 15.4915175,5.67204692 15.4997158,6.91419406 C15.4999523,6.95710967 15.4999523,6.95710967 15.5,7 C15.5,9.52090451 14.5340777,12.111589 12.9179883,14.6199787 C12.3484584,15.5039663 11.7377754,16.313821 11.1275564,17.0311249 C10.9144997,17.2815702 10.7170402,17.5022391 10.5403911,17.6908777 C10.4358029,17.8025645 10.3623853,17.8778048 10.3253512,17.9143634 C10.0291161,18.2331673 9.41484636,18.5 8.9906311,18.5 L7.0093689,18.5 C6.59080843,18.5 5.98194778,18.2258269 5.71364227,17.9146561 C5.66213668,17.8588317 5.58703389,17.7761053 5.4807125,17.6555634 C5.30200204,17.4529504 5.10247221,17.2193106 4.88735491,16.9580823 C4.27213719,16.2109907 3.656779,15.394289 3.08320773,14.5359605 C2.09721248,13.0604546 1.34127053,11.6205479 0.906388115,10.2835472 C0.639104683,9.46181216 0.5,8.69692293 0.5,8 C0.5,7.56658708 0.519280284,7.10494686 0.556403808,6.61890492 C0.63408435,5.60186781 0.786470164,4.51217341 0.991682584,3.40118912 C1.09968656,2.81647439 1.21542088,2.26333889 1.3310756,1.7595034 C1.35796875,1.64234673 1.3824953,1.53794489 1.4041953,1.44733409 Z"/>
                                                                <polygon id="osa-high-H" fill="#FFFFFF"
                                                                         mask="url(#osa-high-Rectangle-2)"
                                                                         points="5 12 7 12 7 9.5 9 9.5 9 12 11 12 11 5 9 5 9 7.5 7 7.5 7 5 5 5"/>
                                                            </g>
                                                        </g>
                                                    </g>
                                                </g>
                                            </svg>
                                        </div>
                                        <div class="bar-title">High -</div>
                                        <div class="bar-count"
                                             id="osa-bar-count-high">${dependencyResult.highVulnerability}</div>
                                    </div>
                                </div>
                            </li>

                            <!--osa-medium-->
                            <li>
                                            <span class="bar-2" id="osa-bar-med"
                                                  style="height: ${dependencyMediumTotalHeight}px">
                                                <div id="osa-tooltip-med">
                                                    <#if config.osaThresholdsEnabled && config.osaMediumThreshold??>
                                                        <@thresholdTooltip threshold=config.osaMediumThreshold count=dependencyResult.mediumVulnerability/>
                                                    </#if>
                                                </div>
                                            </span>
                                <div class="bar-title-wrapper">
                                    <div class="bar-title-container">
                                        <div class="bar-title-icon">
                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                 xmlns:xlink="http://www.w3.org/1999/xlink" width="16"
                                                 height="20"
                                                 viewBox="0 0 16 20">
                                                <title>Med</title>
                                                <defs>
                                                    <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                          id="osa-medium-path-1"/>
                                                    <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                          id="osa-medium-path-2"/>
                                                </defs>
                                                <g fill="none" fill-rule="evenodd">
                                                    <mask id="osa-medium-mask-1" fill="#fff">
                                                        <use xlink:href="#osa-medium-path-1"/>
                                                    </mask>
                                                    <use fill="#FFAC00" xlink:href="#osa-medium-path-1"/>
                                                    <path stroke="#E49B16"
                                                          d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                    <path fill="#D79201" mask="url(#osa-medium-mask-1)"
                                                          d="M8 0h8v20H8z"/>
                                                    <mask id="osa-medium-mask-2" fill="#fff">
                                                        <use xlink:href="#osa-medium-path-2"/>
                                                    </mask>
                                                    <path stroke="#D49100"
                                                          d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                    <path fill="#472F00" mask="url(#osa-medium-mask-2)"
                                                          d="M4.28 12.632h1.9v-4.21l1.78 2.862H8L9.79 8.4v4.232h1.93v-7.37H9.67L8 8.117 6.33 5.263H4.28"/>
                                                </g>
                                            </svg>
                                        </div>
                                        <div class="bar-title">Medium -</div>
                                        <div class="bar-count"
                                             id="osa-bar-count-med">${dependencyResult.mediumVulnerability}</div>
                                    </div>
                                </div>
                            </li>

                            <!--osa-low-->
                            <li>
                                            <span class="bar-3" id="osa-bar-low" style="height: ${dependencyLowTotalHeight}px">
                                                <div id="osa-tooltip-low">
                                                    <#if config.osaThresholdsEnabled && config.osaLowThreshold??>
                                                        <@thresholdTooltip threshold=config.osaLowThreshold count=dependencyResult.lowVulnerability/>
                                                    </#if>
                                                </div>
                                            </span>
                                <div class="bar-title-wrapper">
                                    <div class="bar-title-container">
                                        <div class="bar-title-icon">
                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                 xmlns:xlink="http://www.w3.org/1999/xlink" width="16"
                                                 height="19"
                                                 viewBox="0 0 16 19">
                                                <title>Low</title>
                                                <defs>
                                                    <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                          id="osa-low-path-1"/>
                                                    <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                          id="osa-low-path-2"/>
                                                </defs>
                                                <g fill="none" fill-rule="evenodd">
                                                    <path d="M7.96 17.32L8 .015l-6.5 1s-.96 4.5-.96 8.75c1.272 4.602 5.968 9.25 5.968 9.25h.163l1.29-1.695z"
                                                          fill="#EDEFF5"/>
                                                    <mask id="osa-low-mask-1" fill="#fff">
                                                        <use xlink:href="#osa-low-path-1"/>
                                                    </mask>
                                                    <use fill="#FFEB3B" xlink:href="#osa-low-path-1"/>
                                                    <path stroke="#E4D200"
                                                          d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                    <path fill="#DDCE00" mask="url(#osa-low-mask-1)"
                                                          d="M8-8h10v32H8z"/>
                                                    <mask id="osa-low-mask-2" fill="#fff">
                                                        <use xlink:href="#osa-low-path-2"/>
                                                    </mask>
                                                    <path stroke="#E4D200"
                                                          d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                    <path fill="#605900" mask="url(#osa-low-mask-2)"
                                                          d="M5.54 12h5.33v-1.7H7.48V5H5.54"/>
                                                </g>
                                            </svg>
                                        </div>
                                        <div class="bar-title">Low -</div>
                                        <div class="bar-count"
                                             id="osa-bar-count-low">${dependencyResult.lowVulnerability}</div>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </#if>
        </div>
        </#if>
    </div>
</div>

        <#--<#if config.dependencyScannerType == "SCA">
            <div class="osa-summary <#if !config.sastEnabled>sast-summary chart-large</#if>" id="osa-summary">
                <div class="summary-report-title osa">
                    <div class="summary-title-text osa">SCA Vulnerabilities & Libraries</div>
                    <#if sca.scaResultReady>
                        <div id="osa-title-links" class="title-links">
                            <div class="link-to-result summary-link">
                                <a href="${sca.webReportLink}" class="html-report"
                                   id="osa-summary-html-link">
                                    <div class="results-link-icon link-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="14"
                                             viewBox="0 0 12 14">
                                            <title>analize</title>
                                            <g fill="none" fill-rule="evenodd">
                                                <circle stroke="#4A90E2" stroke-width="2" cx="5" cy="5" r="4"/>
                                                <path fill="#4A90E2" d="M6.366 8.366l1.732-1 3.268 5.66-1.732 1z"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="link-text">Results</div>
                                </a>
                            </div>
                        </div>
                    <#else>
                        <div id="no-scan-message-osa-failed" class="no-scan-message-container osa-scan-failed">
                            <div class="no-scan-message">
                                <div class="no-scan-message-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg"
                                         xmlns:xlink="http://www.w3.org/1999/xlink"
                                         width="48px" height="37px" viewBox="0 0 48 37"
                                         version="1.1">
                                        <!-- Generator: Sketch 45.2 (43514) - http:/*www.bohemiancoding.com/sketch &ndash;&gt;
                                        */
                                        <title>scan_failed</title>
                                        <desc>Created with Sketch.</desc>
                                        <defs/>
                                        <g id="Page-1" stroke="none" stroke-width="1" fill="none"
                                           fill-rule="evenodd">
                                            <g id="Jenkins-OSA-empty"
                                               transform="translate(-936.000000, -335.000000)">
                                                <g id="Group-2" transform="translate(683.000000, 179.000000)">
                                                    <g id="Group" transform="translate(81.000000, 155.202941)">
                                                        <g id="Group-3" transform="translate(171.000000, 0.000000)">
                                                            <g transform="translate(2.000606, 1.714748)">
                                                                <path d="M24.3114123,0.619831205 L44.98533,32.7440758 L44.98533,32.7440758 C45.2842135,33.208497 45.1500186,33.8272778 44.6855974,34.1261613 C44.5242092,34.2300245 44.336342,34.2852522 44.1444209,34.2852522 L1.8977196,34.2852522 L1.8977196,34.2852522 C1.34543485,34.2852522 0.897719599,33.837537 0.897719599,33.2852522 C0.897719599,33.0866799 0.956838199,32.8926033 1.0675421,32.7277531 L22.6403257,0.60350849 L22.6403257,0.60350849 C22.948224,0.145014117 23.569508,0.0229318695 24.0280024,0.330830157 C24.1411852,0.406837189 24.2376306,0.50518529 24.3114123,0.619831205 Z"
                                                                      id="Triangle" stroke="#373050"
                                                                      stroke-width="2"/>
                                                                <rect id="Rectangle-6" fill="#373050" x="21.9993936"
                                                                      y="11.2852522" width="3" height="13"
                                                                      rx="1.5"/>
                                                                <path d="M23.4993936,26.2852522 L23.4993936,26.2852522 L23.4993936,26.2852522 C24.3278207,26.2852522 24.9993936,26.9568251 24.9993936,27.7852522 L24.9993936,27.7852522 L24.9993936,27.7852522 C24.9993936,28.6136794 24.3278207,29.2852522 23.4993936,29.2852522 L23.4993936,29.2852522 L23.4993936,29.2852522 C22.6709664,29.2852522 21.9993936,28.6136794 21.9993936,27.7852522 L21.9993936,27.7852522 L21.9993936,27.7852522 C21.9993936,26.9568251 22.6709664,26.2852522 23.4993936,26.2852522 Z"
                                                                      id="Rectangle-6-Copy" fill="#373050"/>
                                                            </g>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </svg>
                                </div>
                                <div class="no-scan-message-text">SCA scan failed</div>
                            </div>
                        </div>
                    </#if>
                </div>
                <#if sca.scaResultReady>
                    <div class="osa-results" id="osa-results">
                        <!--osa-libraries-count&ndash;&gt;
                        <div class="osa-libraries">
                            <div class="osa-libraries-title">Libraries:</div>
                            <!--osa-libs-vulnerable&ndash;&gt;
                            <div class="libraries-vulnerable">
                                <div class="libraries-icon-number">
                                    <div class="libraries-vulnerable-number"
                                         id="vulnerable-libraries">${sca.summary.totalOutdatedPackages}</div>
                                </div>
                                <div class="libraries-vulnerable-text">
                                    Vulnerable and Outdated Libraries
                                </div>
                            </div>
                            <!--osa-libs-ok&ndash;&gt;
                            <div class="libraries-vulnerable">
                                <div class="libraries-icon-number">
                                    <div class="libraries-vulnerable-number"
                                         id="vulnerable-libraries">${sca.summary.getTotalOkLibraries()}</div>
                                </div>
                                <div class="libraries-vulnerable-text">
                                    No Known Vulnerability Libraries
                                </div>
                            </div>
                        </div>
                        <!--sca-chart&ndash;&gt;
                        <div class="osa-chart">
                            <ul class="osa-chart chart">
                                <!--sca-high&ndash;&gt;
                                <li>
                                            <span class="bar-1" id="osa-bar-high"
                                                  style="height: ${scaHighTotalHeight}px">
                                                <div id="osa-tooltip-high">
                                                    <#if config.osaThresholdsEnabled && config.osaHighThreshold??>
                                                        <@thresholdTooltip threshold=config.osaHighThreshold count=sca.summary.highVulnerabilityCount/>
                                                    </#if>
                                                </div>
                                            </span>

                                    <div class="bar-title-wrapper">
                                        <div class="bar-title-container">
                                            <div class="bar-title-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                     xmlns:xlink="http://www.w3.org/1999/xlink" width="16px"
                                                     height="19px"
                                                     viewBox="0 0 16 19"
                                                     version="1.1">
                                                    <!-- Generator: Sketch 43.2 (39069) - http://www.bohemiancoding.com/sketch &ndash;&gt;
                                                    <title>Med</title>
                                                    <desc>Created with Sketch.</desc>
                                                    <defs>
                                                        <path d="M1,1 L8,0 L15,1 C15,1 16,4.01515152 16,7 C16,13.0151515 10.6766131,18.2701936 10.6766131,18.2701936 C10.30293,18.6732545 9.55664682,19 8.9906311,19 L7.0093689,19 C6.45190985,19 5.70245907,18.6673641 5.33497024,18.2411641 C5.33497024,18.2411641 3.70193273e-12,12.5151515 3.63797881e-12,8 C7.03437308e-13,4.82765152 1,1 1,1 Z"
                                                              id="osa-high-path-1"/>
                                                        <path d="M1,1 L8,0 L15,1 C15,1 16,4.01515152 16,7 C16,13.0151515 10.6766131,18.2701936 10.6766131,18.2701936 C10.30293,18.6732545 9.55664682,19 8.9906311,19 L7.0093689,19 C6.45190985,19 5.70245907,18.6673641 5.33497024,18.2411641 C5.33497024,18.2411641 3.70193273e-12,12.5151515 3.63797881e-12,8 C7.03437308e-13,4.82765152 1,1 1,1 Z"
                                                              id="osa-high-path-2"/>
                                                    </defs>
                                                    <g id="osa-high-Page-1" stroke="none" stroke-width="1"
                                                       fill="none"
                                                       fill-rule="evenodd">
                                                        <g id="osa-high-Icons"
                                                           transform="translate(-47.000000, -88.000000)">
                                                            <g id="osa-high-High"
                                                               transform="translate(47.000000, 88.000000)">
                                                                <g id="osa-high-Vonerability">
                                                                    <mask id="osa-high-mask-1" fill="white">
                                                                        <use xlink:href="#osa-high-path-1"/>
                                                                    </mask>
                                                                    <g id="osa-high-Rectangle-1">
                                                                        <use fill="#D82D49" fill-rule="evenodd"
                                                                             xlink:href="#osa-high-path-1"/>
                                                                        <path stroke="#BB1A34" stroke-width="1"
                                                                              d="M1.4041953,1.44733409 L8,0.505076272 L14.6160396,1.45022478 C14.6341112,1.51124347 14.6539641,1.5795116 14.6753578,1.65465958 C14.7899552,2.05719756 14.9047222,2.50600605 15.0118679,2.98897331 C15.3098751,4.33226343 15.4915175,5.67204692 15.4997158,6.91419406 C15.4999523,6.95710967 15.4999523,6.95710967 15.5,7 C15.5,9.52090451 14.5340777,12.111589 12.9179883,14.6199787 C12.3484584,15.5039663 11.7377754,16.313821 11.1275564,17.0311249 C10.9144997,17.2815702 10.7170402,17.5022391 10.5403911,17.6908777 C10.4358029,17.8025645 10.3623853,17.8778048 10.3253512,17.9143634 C10.0291161,18.2331673 9.41484636,18.5 8.9906311,18.5 L7.0093689,18.5 C6.59080843,18.5 5.98194778,18.2258269 5.71364227,17.9146561 C5.66213668,17.8588317 5.58703389,17.7761053 5.4807125,17.6555634 C5.30200204,17.4529504 5.10247221,17.2193106 4.88735491,16.9580823 C4.27213719,16.2109907 3.656779,15.394289 3.08320773,14.5359605 C2.09721248,13.0604546 1.34127053,11.6205479 0.906388115,10.2835472 C0.639104683,9.46181216 0.5,8.69692293 0.5,8 C0.5,7.56658708 0.519280284,7.10494686 0.556403808,6.61890492 C0.63408435,5.60186781 0.786470164,4.51217341 0.991682584,3.40118912 C1.09968656,2.81647439 1.21542088,2.26333889 1.3310756,1.7595034 C1.35796875,1.64234673 1.3824953,1.53794489 1.4041953,1.44733409 Z"/>
                                                                    </g>
                                                                    <rect id="osa-high-Rectangle-2" fill="#BB1A34"
                                                                          mask="url(#osa-high-mask-1)"
                                                                          x="8" y="0" width="8" height="20"/>
                                                                    <mask id="osa-high-mask-2" fill="white">
                                                                        <use xlink:href="#osa-high-path-2"/>
                                                                    </mask>
                                                                    <path stroke="#BB1A34"
                                                                          d="M1.4041953,1.44733409 L8,0.505076272 L14.6160396,1.45022478 C14.6341112,1.51124347 14.6539641,1.5795116 14.6753578,1.65465958 C14.7899552,2.05719756 14.9047222,2.50600605 15.0118679,2.98897331 C15.3098751,4.33226343 15.4915175,5.67204692 15.4997158,6.91419406 C15.4999523,6.95710967 15.4999523,6.95710967 15.5,7 C15.5,9.52090451 14.5340777,12.111589 12.9179883,14.6199787 C12.3484584,15.5039663 11.7377754,16.313821 11.1275564,17.0311249 C10.9144997,17.2815702 10.7170402,17.5022391 10.5403911,17.6908777 C10.4358029,17.8025645 10.3623853,17.8778048 10.3253512,17.9143634 C10.0291161,18.2331673 9.41484636,18.5 8.9906311,18.5 L7.0093689,18.5 C6.59080843,18.5 5.98194778,18.2258269 5.71364227,17.9146561 C5.66213668,17.8588317 5.58703389,17.7761053 5.4807125,17.6555634 C5.30200204,17.4529504 5.10247221,17.2193106 4.88735491,16.9580823 C4.27213719,16.2109907 3.656779,15.394289 3.08320773,14.5359605 C2.09721248,13.0604546 1.34127053,11.6205479 0.906388115,10.2835472 C0.639104683,9.46181216 0.5,8.69692293 0.5,8 C0.5,7.56658708 0.519280284,7.10494686 0.556403808,6.61890492 C0.63408435,5.60186781 0.786470164,4.51217341 0.991682584,3.40118912 C1.09968656,2.81647439 1.21542088,2.26333889 1.3310756,1.7595034 C1.35796875,1.64234673 1.3824953,1.53794489 1.4041953,1.44733409 Z"/>
                                                                    <polygon id="osa-high-H" fill="#FFFFFF"
                                                                             mask="url(#osa-high-Rectangle-2)"
                                                                             points="5 12 7 12 7 9.5 9 9.5 9 12 11 12 11 5 9 5 9 7.5 7 7.5 7 5 5 5"/>
                                                                </g>
                                                            </g>
                                                        </g>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="bar-title">High -</div>
                                            <div class="bar-count"
                                                 id="osa-bar-count-high">${sca.summary.highVulnerabilityCount}</div>
                                        </div>
                                    </div>
                                </li>

                                <!--sca-medium&ndash;&gt;
                                <li>
                                            <span class="bar-2" id="osa-bar-med"
                                                  style="height: ${scaMediumTotalHeight}px">
                                                <div id="osa-tooltip-med">
                                                    <#if config.osaThresholdsEnabled && config.osaMediumThreshold??>
                                                        <@thresholdTooltip threshold=config.osaMediumThreshold count=sca.summary.mediumVulnerabilityCount/>
                                                    </#if>
                                                </div>
                                            </span>
                                    <div class="bar-title-wrapper">
                                        <div class="bar-title-container">
                                            <div class="bar-title-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                     xmlns:xlink="http://www.w3.org/1999/xlink" width="16"
                                                     height="20"
                                                     viewBox="0 0 16 20">
                                                    <title>Med</title>
                                                    <defs>
                                                        <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                              id="osa-medium-path-1"/>
                                                        <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                              id="osa-medium-path-2"/>
                                                    </defs>
                                                    <g fill="none" fill-rule="evenodd">
                                                        <mask id="osa-medium-mask-1" fill="#fff">
                                                            <use xlink:href="#osa-medium-path-1"/>
                                                        </mask>
                                                        <use fill="#FFAC00" xlink:href="#osa-medium-path-1"/>
                                                        <path stroke="#E49B16"
                                                              d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                        <path fill="#D79201" mask="url(#osa-medium-mask-1)"
                                                              d="M8 0h8v20H8z"/>
                                                        <mask id="osa-medium-mask-2" fill="#fff">
                                                            <use xlink:href="#osa-medium-path-2"/>
                                                        </mask>
                                                        <path stroke="#D49100"
                                                              d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                        <path fill="#472F00" mask="url(#osa-medium-mask-2)"
                                                              d="M4.28 12.632h1.9v-4.21l1.78 2.862H8L9.79 8.4v4.232h1.93v-7.37H9.67L8 8.117 6.33 5.263H4.28"/>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="bar-title">Medium -</div>
                                            <div class="bar-count"
                                                 id="osa-bar-count-med">${sca.summary.mediumVulnerabilityCount}</div>
                                        </div>
                                    </div>
                                </li>

                                <!--osa-low&ndash;&gt;
                                <li>
                                            <span class="bar-3" id="osa-bar-low" style="height: ${scaLowTotalHeight}px">
                                                <div id="osa-tooltip-low">
                                                    <#if config.osaThresholdsEnabled && config.osaLowThreshold??>
                                                        <@thresholdTooltip threshold=config.osaLowThreshold count=sca.summary.lowVulnerabilityCount/>
                                                    </#if>
                                                </div>
                                            </span>
                                    <div class="bar-title-wrapper">
                                        <div class="bar-title-container">
                                            <div class="bar-title-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                     xmlns:xlink="http://www.w3.org/1999/xlink" width="16"
                                                     height="19"
                                                     viewBox="0 0 16 19">
                                                    <title>Low</title>
                                                    <defs>
                                                        <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                              id="osa-low-path-1"/>
                                                        <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                              id="osa-low-path-2"/>
                                                    </defs>
                                                    <g fill="none" fill-rule="evenodd">
                                                        <path d="M7.96 17.32L8 .015l-6.5 1s-.96 4.5-.96 8.75c1.272 4.602 5.968 9.25 5.968 9.25h.163l1.29-1.695z"
                                                              fill="#EDEFF5"/>
                                                        <mask id="osa-low-mask-1" fill="#fff">
                                                            <use xlink:href="#osa-low-path-1"/>
                                                        </mask>
                                                        <use fill="#FFEB3B" xlink:href="#osa-low-path-1"/>
                                                        <path stroke="#E4D200"
                                                              d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                        <path fill="#DDCE00" mask="url(#osa-low-mask-1)"
                                                              d="M8-8h10v32H8z"/>
                                                        <mask id="osa-low-mask-2" fill="#fff">
                                                            <use xlink:href="#osa-low-path-2"/>
                                                        </mask>
                                                        <path stroke="#E4D200"
                                                              d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                        <path fill="#605900" mask="url(#osa-low-mask-2)"
                                                              d="M5.54 12h5.33v-1.7H7.48V5H5.54"/>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="bar-title">Low -</div>
                                            <div class="bar-count"
                                                 id="osa-bar-count-low">${sca.summary.mediumVulnerabilityCount}</div>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </#if>
            </div>
        </#if>
    </div>
</div>-->

    <#if config.isSastEnabled() && config.generateXmlReport &&sast.sastResultsReady>
        <#if sast.high gt 0 || sast.medium gt 0 || sast.low gt 0>
            <div id="sast-full" class="sast-full full-results-section">
                <div class="summary-table-row cxsast-full">
                    <div class="title-column">
                        <div class="summary-title">
                            <div class="sum1">CxSAST</div>
                            <div class="sum1">Full Report</div>
                        </div>
                        <div class="detailed-report">
                            <div class="full-downloads sast-downloads">
                                <div class="report-link link-to-result">
                                    <a href="${sast.sastScanLink}" class="pdf-report" id="sast-code-viewer-link"
                                       target="_top">
                                        <div class="link-to-result">
                                            <div class="results-link-icon link-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="14"
                                                     viewBox="0 0 12 14"><title>analize</title>
                                                    <g fill="none" fill-rule="evenodd">
                                                        <circle stroke="#4A90E2" stroke-width="2" cx="5" cy="5" r="4"/>
                                                        <path fill="#4A90E2"
                                                              d="M6.366 8.366l1.732-1 3.268 5.66-1.732 1z"/>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="link-text">Analyze Results</div>
                                        </div>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="main-column">
                        <div class="full-start-end">
                            <div class="full-start">
                                <div class="full-start-end-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                                         height="26px" version="1.1" viewBox="0 0 23 26" width="23px">
                                        <title>Icon</title>
                                        <desc>Created with Sketch.</desc>
                                        <defs>
                                            <rect height="23" rx="1.6" id="sast-full-start-rect" width="23" x="0"
                                                  y="2"/>
                                            <mask height="23" maskUnits="objectBoundingBox" id="full-start-mask-1"
                                                  width="23"
                                                  maskContentUnits="userSpaceOnUse" fill="white" x="0"
                                                  y="0">
                                                <use xlink:href="#sast-full-start-rect"/>
                                            </mask>
                                        </defs>
                                        <g stroke-width="1" fill-rule="evenodd" id="sast-full-start-Page-1"
                                           stroke="none"
                                           fill="none">
                                            <g transform="translate(-684.000000, -708.000000)" id="sast-full-start-1"
                                               stroke="#373050">
                                                <g transform="translate(273.000000, 695.000000)" id="sast-full-start-2">
                                                    <g transform="translate(411.000000, 9.000000)"
                                                       id="sast-full-start-3">
                                                        <g transform="translate(0.000000, 5.000000)"
                                                           id="sast-full-start-Icon">
                                                            <use mask="url(#full-start-mask-1)" stroke-width="4"
                                                                 id="sast-full-start-Rectangle-1"
                                                                 xlink:href="#sast-full-start-rect"/>
                                                            <path stroke-width="2" id="sast-full-start-Line-1"
                                                                  d="M5,0 L5,2.99971994"
                                                                  stroke-linecap="square"/>
                                                            <path stroke-width="2" id="sast-full-start-Line-2"
                                                                  d="M18,0 L18,2.99971994"
                                                                  stroke-linecap="square"/>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </svg>
                                </div>
                                <div class="full-start-end-text-date">
                                    <div class="full-start-end-text">
                                        Start:
                                    </div>
                                    <div class="full-start-end-date"
                                         id="sast-full-start-date">${sast.scanStartTime}</div>
                                </div>
                            </div>

                            <!--sast-full-end-->
                            <div class="full-end">
                                <div class="full-start-end-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                                         height="26px" version="1.1" viewBox="0 0 23 26" width="23px">
                                        <title>Icon</title>
                                        <desc>Created with Sketch.</desc>
                                        <defs>
                                            <rect height="23" rx="1.6" id="sast-full-end-rect" width="23" x="0" y="2"/>
                                            <mask height="23" maskUnits="objectBoundingBox"
                                                  id="sast-full-end-mask" width="23"
                                                  maskContentUnits="userSpaceOnUse" fill="white" x="0"
                                                  y="0">
                                                <use xlink:href="#sast-full-end-rect"/>
                                            </mask>
                                        </defs>
                                        <g stroke-width="1" fill-rule="evenodd" id="sast-full-end-Page-1" stroke="none"
                                           fill="none">
                                            <g transform="translate(-684.000000, -708.000000)" id="sast-full-end-1"
                                               stroke="#373050">
                                                <g transform="translate(273.000000, 695.000000)" id="sast-full-end-2">
                                                    <g transform="translate(411.000000, 9.000000)" id="sast-full-end-3">
                                                        <g transform="translate(0.000000, 5.000000)"
                                                           id="sast-full-end-Icon">
                                                            <use mask="url(#sast-full-end-mask)" stroke-width="4"
                                                                 id="sast-full-end-use"
                                                                 xlink:href="#sast-full-end-rect"/>
                                                            <path stroke-width="2"
                                                                  id="sast-full-end-Line-1" d="M5,0 L5,2.99971994"
                                                                  stroke-linecap="square"/>
                                                            <path stroke-width="2"
                                                                  id="sast-full-end-Line-2" d="M18,0 L18,2.99971994"
                                                                  stroke-linecap="square"/>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </svg>
                                </div>
                                <div class="full-start-end-text-date">
                                    <div class="full-start-end-text">
                                        End:
                                    </div>
                                    <div class="full-start-end-date" id="sast-full-end-date">${sast.scanEndTime}</div>
                                </div>
                            </div>

                            <!--sast-full-files-->
                            <div class="full-files">
                                <div class="full-start-end-icon files-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                                         height="26px" version="1.1" viewBox="0 0 21 26" width="21px">
                                        <title>file</title>
                                        <desc>Created with Sketch.</desc>
                                        <defs>
                                            <path id="sast-full-files-path"
                                                  d="M15.5147186,0 L1.99456145,0 C0.90234375,0 0,0.89408944 0,1.99700466 L0,24.0029953 C0,25.1050211 0.892995579,26 1.99456145,26 L19.0054385,26 C20.0976562,26 21,25.1059106 21,24.0029953 L21,5.43446766 L20.9745931,5.45987452 L15.5147186,5.55111512e-16 Z"/>
                                            <mask height="26" maskUnits="objectBoundingBox"
                                                  id="sast-full-files-mask" width="21"
                                                  maskContentUnits="userSpaceOnUse" fill="white" x="0" y="0">
                                                <use xlink:href="#sast-full-files-path"/>
                                            </mask>
                                        </defs>
                                        <g stroke-width="1" fill-rule="evenodd" id="sast-full-files-Page-1"
                                           stroke="none"
                                           fill="none">
                                            <g transform="translate(-847.000000, -709.000000)" id="sast-full-files-1">
                                                <g transform="translate(273.000000, 695.000000)" id="sast-full-files-2">
                                                    <g transform="translate(574.000000, 9.000000)"
                                                       id="sast-full-files-3">
                                                        <g transform="translate(0.000000, 5.000000)"
                                                           id="sast-full-files-4">
                                                            <use mask="url(#sast-full-files-mask)" stroke-width="4"
                                                                 id="sast-full-files-use" stroke="#373050"
                                                                 xlink:href="#sast-full-files-path"/>
                                                            <path id="sast-full-files-path-2"
                                                                  d="M13.8888889,0 L14.7777778,0 L21,7 L21,7.5 L21,8 L13,8 L13,0 L13.8888889,0 Z"
                                                                  fill="#373050"/>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </svg>
                                </div>
                                <div class="full-start-end-text-date">
                                    <div class="full-start-end-text">
                                        Files:
                                    </div>
                                    <div class="full-start-end-date" id="sast-full-files">${sast.filesScanned}</div>
                                </div>
                            </div>

                            <!--sast-full-loc-->
                            <div class="full-loc">
                                <div class="full-start-end-icon loc-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg"
                                         xmlns:xlink="http://www.w3.org/1999/xlink" height="29"
                                         id="sast-full-loc-svg"
                                         version="1.1" viewBox="0 0 29 29" width="29">
                                        <title>Icon</title>
                                        <desc>Created with Avocode.</desc>
                                        <defs id="sast-full-loc-defs"/>
                                        <path stroke-miterlimit="50" transform="matrix(1,0,0,1,-1012,-1365)"
                                              stroke-width="2"
                                              id="sast-full-loc-path-1" fill-opacity="0"
                                              stroke-dasharray="0"
                                              d="M1032 1368L1039.95 1378.76L1032 1389.51 " stroke-opacity="1"
                                              fill="#ffffff"
                                              stroke="#373050" stroke-linecap="round"
                                              stroke-linejoin="round"/>
                                        <path stroke-miterlimit="50" transform="matrix(1,0,0,1,-1012,-1365)"
                                              stroke-width="2" id="sast-full-loc-path-2" fill-opacity="0"
                                              stroke-dasharray="0"
                                              d="M1020.95 1368L1013 1378.76L1020.95 1389.51 " stroke-opacity="1"
                                              fill="#ffffff" stroke="#373050" stroke-linecap="round"
                                              stroke-linejoin="round"/>
                                        <path stroke-miterlimit="50" transform="matrix(1,0,0,1,-1012,-1365)"
                                              stroke-width="2" id="sast-full-loc-path-3" fill-opacity="0"
                                              stroke-dasharray="0"
                                              d="M1028.86 1366L1022.9999999999999 1392.54 " stroke-opacity="1"
                                              fill="#ffffff" stroke="#373050" stroke-linecap="round"
                                              stroke-linejoin="round"/>
                                    </svg>
                                </div>
                                <div class="full-start-end-text-date">
                                    <div class="full-start-end-text">
                                        Code Lines:
                                    </div>
                                    <div class="full-start-end-date" id="sast-full-loc">${sast.LOC}</div>
                                </div>
                            </div>
                        </div>

                        <#if sast.high gt 0>
                            <div id="sast-cve-table-high-container">
                                <div class="full-severity-title">
                                    <div class="severity-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg"
                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="19"
                                             viewBox="0 0 16 19"><title>High</title>
                                            <defs>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-5.323 11.27-5.323 11.27-.374.403-1.12.73-1.686.73H7.01c-.558 0-1.308-.333-1.675-.76C5.335 18.24 0 12.516 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-high-path-1"/>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-5.323 11.27-5.323 11.27-.374.403-1.12.73-1.686.73H7.01c-.558 0-1.308-.333-1.675-.76C5.335 18.24 0 12.516 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-high-path-2"/>
                                            </defs>
                                            <g fill="none" fill-rule="evenodd">
                                                <mask id="sast-full-high-mask-1" fill="#fff">
                                                    <use xlink:href="#sast-full-high-path-1"/>
                                                </mask>
                                                <use fill="#D82D49" xlink:href="#sast-full-high-path-1"/>
                                                <path stroke="#BB1A34"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.342.48 2.682.488 3.924V7c0 2.52-.966 5.112-2.582 7.62-.57.884-1.18 1.694-1.79 2.41-.214.252-.41.472-.588.66-.104.113-.178.188-.215.224-.296.32-.91.586-1.334.586H7.01c-.42 0-1.028-.274-1.296-.585-.052-.056-.127-.14-.233-.26-.178-.202-.378-.436-.593-.697-.615-.747-1.23-1.564-1.804-2.422C2.097 13.06 1.34 11.62.906 10.284.64 9.462.5 8.697.5 8c0-.433.02-.895.056-1.38C.634 5.6.786 4.51.992 3.4c.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#BB1A34" mask="url(#sast-full-high-mask-1)"
                                                      d="M8 0h8v20H8z"/>
                                                <mask id="sast-full-high-mask-2" fill="#fff">
                                                    <use xlink:href="#sast-full-high-path-2"/>
                                                </mask>
                                                <path stroke="#BB1A34"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.342.48 2.682.488 3.924V7c0 2.52-.966 5.112-2.582 7.62-.57.884-1.18 1.694-1.79 2.41-.214.252-.41.472-.588.66-.104.113-.178.188-.215.224-.296.32-.91.586-1.334.586H7.01c-.42 0-1.028-.274-1.296-.585-.052-.056-.127-.14-.233-.26-.178-.202-.378-.436-.593-.697-.615-.747-1.23-1.564-1.804-2.422C2.097 13.06 1.34 11.62.906 10.284.64 9.462.5 8.697.5 8c0-.433.02-.895.056-1.38C.634 5.6.786 4.51.992 3.4c.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#FFF" mask="url(#sast-full-high-mask-2)"
                                                      d="M5 12h2V9.5h2V12h2V5H9v2.5H7V5H5"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="severity-title-name">High</div>
                                    <div class="severity-count"> ${sast.high}</div>
                                </div>
                                <table id="sast-cve-table-high" class="cve-table sast-cve-table sast-cve-table-high">
                                    <tr>
                                        <th>Vulnerability</th>
                                        <th>##</th>
                                    </tr>
                                    <#list sast.queryList as query>
                                        <#if query.severity == "High">
                                            <tr>
                                                <td>${query.name}</td>
                                                <td>${query.result?size}</td>
                                            </tr>
                                        </#if>
                                    </#list>
                                </table>
                            </div>
                        </#if>

                        <#if sast.medium gt 0>
                            <div id="sast-cve-table-medium-container">
                                <div class="full-severity-title">
                                    <div class="severity-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg"
                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="20"
                                             viewBox="0 0 16 20"><title>Med</title>
                                            <defs>
                                                <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                      id="sast-full-med-path-1"/>
                                                <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                      id="sast-full-med-path-2"/>
                                            </defs>
                                            <g fill="none" fill-rule="evenodd">
                                                <mask id="sast-full-med-mask-1" fill="#fff">
                                                    <use xlink:href="#sast-full-med-path-1"/>
                                                </mask>
                                                <use fill="#FFAC00" xlink:href="#sast-full-med-path-1"/>
                                                <path stroke="#E49B16"
                                                      d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                <path fill="#D79201" mask="url(#sast-full-med-mask-1)"
                                                      d="M8 0h8v20H8z"/>
                                                <mask id="sast-full-med-mask-2" fill="#fff">
                                                    <use xlink:href="#sast-full-med-path-2"/>
                                                </mask>
                                                <path stroke="#D49100"
                                                      d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                <path fill="#472F00" mask="url(#sast-full-med-mask-2)"
                                                      d="M4.28 12.632h1.9v-4.21l1.78 2.862H8L9.79 8.4v4.232h1.93v-7.37H9.67L8 8.117 6.33 5.263H4.28"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="severity-title-name">Medium</div>
                                    <div class="severity-count">${sast.medium}</div>
                                </div>
                                <table id="sast-cve-table-medium"
                                       class="cve-table sast-cve-table sast-cve-table-medium">
                                    <tr>
                                        <th>Vulnerability</th>
                                        <th>##</th>
                                    </tr>
                                    <#list sast.queryList as query>
                                        <#if query.severity == "Medium">
                                            <tr>
                                                <td>${query.name}</td>
                                                <td>${query.result?size}</td>
                                            </tr>
                                        </#if>
                                    </#list>
                                </table>
                            </div>
                        </#if>

                        <#if sast.low gt 0>
                            <div id="sast-cve-table-low-container">
                                <div class="full-severity-title">
                                    <div class="severity-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg"
                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="19"
                                             viewBox="0 0 16 19"><title>Low</title>
                                            <defs>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-low-path-1"/>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-low-path-2"/>
                                            </defs>
                                            <g fill="none" fill-rule="evenodd">
                                                <path d="M7.96 17.32L8 .015l-6.5 1s-.96 4.5-.96 8.75c1.272 4.602 5.968 9.25 5.968 9.25h.163l1.29-1.695z"
                                                      fill="#EDEFF5"/>
                                                <mask id="sast-full-low-mask-1" fill="#fff">
                                                    <use xlink:href="#sast-full-low-path-1"/>
                                                </mask>
                                                <use fill="#FFEB3B" xlink:href="#sast-full-low-path-1"/>
                                                <path stroke="#E4D200"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#DDCE00" mask="url(#sast-full-low-mask-1)"
                                                      d="M8-8h10v32H8z"/>
                                                <mask id="sast-full-low-mask-2" fill="#fff">
                                                    <use xlink:href="#sast-full-low-path-2"/>
                                                </mask>
                                                <path stroke="#E4D200"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#605900" mask="url(#sast-full-low-mask-2)"
                                                      d="M5.54 12h5.33v-1.7H7.48V5H5.54"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="severity-title-name">Low</div>
                                    <div class="severity-count"> ${sast.low}</div>
                                </div>
                                <table id="sast-cve-table-low" class="cve-table sast-cve-table sast-cve-table-low">
                                    <tr>
                                        <th>Vulnerability</th>
                                        <th>##</th>
                                    </tr>
                                    <#list sast.queryList as query>
                                        <#if query.severity == "Low">
                                            <tr>
                                                <td>${query.name}</td>
                                                <td>${query.result?size}</td>
                                            </tr>
                                        </#if>
                                    </#list>
                                </table>
                            </div>
                        </#if>
                    </div>
                </div>
            </div>
        </#if>
    </#if>

    <#if ( config.isOsaEnabled() || config.isAstScaEnabled()) && dependencyResult.resultReady>
        <#if dependencyResult.dependencyHighCVEReportTable?size gt 0 || dependencyResult.dependencyMediumCVEReportTable?size gt 0 || dependencyResult.dependencyLowCVEReportTable?size gt 0>
            <div id="osa-full" class="osa-full full-results-section">
                <div class="summary-table-row cxosa-full">
                    <div class="title-column">
                        <div class="summary-title">
                        <#if dependencyResult.scannerType=="AST_SCA">
                            <div class="sum1">CxSCA</div>
                            <#else>
                                <div class="sum1">Cx${dependencyResult.scannerType}</div>
                        </#if>
                            <div class="sum1">Full Report</div>
                        </div>
                        <div class="detailed-report">
                            <div class="full-downloads osa-downloads">
                                <div class="report-link">
                                    <a href="${dependencyResult.summaryLink}" class="html-report" id="osa-html-link">
                                        <div class="link-to-result">
                                            <div class="results-link-icon link-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="14"
                                                     viewBox="0 0 12 14">
                                                    <title>analyze</title>
                                                    <g fill="none" fill-rule="evenodd">
                                                        <circle stroke="#4A90E2" stroke-width="2" cx="5" cy="5" r="4"/>
                                                        <path fill="#4A90E2"
                                                              d="M6.366 8.366l1.732-1 3.268 5.66-1.732 1z"/>
                                                    </g>
                                                </svg>
                                            </div>
                                            <div class="link-text">Analyze Results</div>
                                        </div>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="main-column">
                        <div class="full-start-end">
                            <div class="full-start">
                                <div class="full-start-end-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                                         height="26px" version="1.1" viewBox="0 0 23 26" width="23px">
                                        <title>Icon</title>
                                        <desc>Created with Sketch.</desc>
                                        <defs>
                                            <rect height="23" rx="1.6" id="osa-full-start-rect" width="23" x="0" y="2"/>
                                            <mask height="23" maskUnits="objectBoundingBox" id="osa-full-start-mask"
                                                  width="23"
                                                  maskContentUnits="userSpaceOnUse" fill="white" x="0"
                                                  y="0">
                                                <use xlink:href="#osa-full-start-rect"/>
                                            </mask>
                                        </defs>
                                        <g stroke-width="1" fill-rule="evenodd" id="osa-full-start-Page-1" stroke="none"
                                           fill="none">
                                            <g transform="translate(-684.000000, -708.000000)" id="osa-full-start-1"
                                               stroke="#373050">
                                                <g transform="translate(273.000000, 695.000000)" id="osa-full-start-2">
                                                    <g transform="translate(411.000000, 9.000000)"
                                                       id="osa-full-start-3">
                                                        <g transform="translate(0.000000, 5.000000)"
                                                           id="osa-full-start-Icon">
                                                            <use mask="url(#osa-full-start-mask)" stroke-width="4"
                                                                 id="osa-full-start-use"
                                                                 xlink:href="#osa-full-end-rect"/>
                                                            <path stroke-width="2" id="osa-full-start-Line-1"
                                                                  d="M5,0 L5,2.99971994"
                                                                  stroke-linecap="square"/>
                                                            <path stroke-width="2" id="osa-full-start-Line-2"
                                                                  d="M18,0 L18,2.99971994"
                                                                  stroke-linecap="square"/>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </svg>
                                </div>
                                <div class="full-start-end-text-date">
                                    <div class="full-start-end-text">
                                        Start:
                                    </div>
                                    <div class="full-start-end-date" id="osa-full-start-date">${dependencyResult.scanStartTime}</div>
                                </div>
                            </div>

                            <!--osa-full-end-->
                            <div class="full-end">
                                <div class="full-start-end-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                                         height="26px" version="1.1" viewBox="0 0 23 26" width="23px">
                                        <title>Icon</title>
                                        <desc>Created with Sketch.</desc>
                                        <defs>
                                            <rect height="23" rx="1.6" id="osa-full-end-rect" width="23" x="0" y="2"/>
                                            <mask height="23" maskUnits="objectBoundingBox" id="osa-full-end-mask"
                                                  width="23"
                                                  maskContentUnits="userSpaceOnUse" fill="white" x="0"
                                                  y="0">
                                                <use xlink:href="#osa-full-end-rect"/>
                                            </mask>
                                        </defs>
                                        <g stroke-width="1" fill-rule="evenodd"
                                           id="osa-full-end-Page-1" stroke="none" fill="none">
                                            <g transform="translate(-684.000000, -708.000000)" id="osa-full-end-1"
                                               stroke="#373050">
                                                <g transform="translate(273.000000, 695.000000)" id="osa-full-end-2">
                                                    <g transform="translate(411.000000, 9.000000)" id="osa-full-end-3">
                                                        <g transform="translate(0.000000, 5.000000)"
                                                           id="osa-full-end-Icon">
                                                            <use mask="url(#osa-full-end-mask)" stroke-width="4"
                                                                 id="osa-full-end-mask-use"
                                                                 xlink:href="#osa-full-end-rect"/>
                                                            <path stroke-width="2" id="osa-full-end-mask-Line-1"
                                                                  d="M5,0 L5,2.99971994"
                                                                  stroke-linecap="square"/>
                                                            <path stroke-width="2" id="osa-full-end-Line-2"
                                                                  d="M18,0 L18,2.99971994"
                                                                  stroke-linecap="square"/>
                                                        </g>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </svg>
                                </div>
                                <div class="full-start-end-text-date">
                                    <div class="full-start-end-text">
                                        End:
                                    </div>
                                    <div class="full-start-end-date" id="osa-full-end-date">${dependencyResult.scanEndTime}</div>
                                </div>
                            </div>

                            <!--osa-full-files-->
                            <div class="full-files">
                                <div class="full-start-end-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg"
                                         xmlns:xlink="http://www.w3.org/1999/xlink" height="27"
                                         id="osa-full-files-svg"
                                         version="1.1" viewBox="0 0 32 27" width="32">
                                        <title>Combined Shape</title>
                                        <desc>Created with Avocode.</desc>
                                        <defs id="osa-full-files-defs">
                                            <clipPath id="osa-full-files-clipPath-1">
                                                <path id="osa-full-files-Path-1"
                                                      d="M848 2095C848 2094.82964 848.0142 2094.6626 848.04148 2094.5C848.0142 2094.3374 848 2094.17036 848 2094V2091C848 2089.34315 849.34315 2088 851 2088H857C858.65685 2088 860.29137 2089.31116 860.65079 2090.92856L860.8888900000001 2092H877.0000000000001C878.6568500000001 2092 880.0000000000001 2093.34315 880.0000000000001 2095V2112C880.0000000000001 2113.65685 878.6568500000001 2115 877.0000000000001 2115H851.0000000000001C849.3431500000002 2115 848.0000000000001 2113.65685 848.0000000000001 2112Z "
                                                      fill="#ffffff"/>
                                            </clipPath>
                                        </defs>
                                        <path stroke-dasharray="0"
                                              d="M848 2095C848 2094.82964 848.0142 2094.6626 848.04148 2094.5C848.0142 2094.3374 848 2094.17036 848 2094V2091C848 2089.34315 849.34315 2088 851 2088H857C858.65685 2088 860.29137 2089.31116 860.65079 2090.92856L860.8888900000001 2092H877.0000000000001C878.6568500000001 2092 880.0000000000001 2093.34315 880.0000000000001 2095V2112C880.0000000000001 2113.65685 878.6568500000001 2115 877.0000000000001 2115H851.0000000000001C849.3431500000002 2115 848.0000000000001 2113.65685 848.0000000000001 2112Z "
                                              stroke-opacity="1" stroke="#373050" stroke-linecap="butt"
                                              stroke-linejoin="miter" stroke-miterlimit="50"
                                              transform="matrix(1,0,0,1,-848,-2088)"
                                              stroke-width="4"
                                              id="osa-full-files-Path-2" fill-opacity="0" fill="#ffffff"
                                              clip-path="url(#osa-full-files-clipPath-1)"/>
                                    </svg>
                                </div>
                                <div class="full-start-end-text-date">
                                    <div class="full-start-end-text">
                                        Libraries:
                                    </div>
                                    <div class="full-start-end-date"
                                         id="osa-full-files">${dependencyResult.totalLibraries}</div>
                                </div>
                            </div>
                        </div>
                        <#if dependencyResult.dependencyHighCVEReportTable?size gt 0>
                            <div id="osa-cve-table-high-container">
                                <div class="full-severity-title">
                                    <div class="severity-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg"
                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="19"
                                             viewBox="0 0 16 19"><title>High</title>
                                            <defs>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-5.323 11.27-5.323 11.27-.374.403-1.12.73-1.686.73H7.01c-.558 0-1.308-.333-1.675-.76C5.335 18.24 0 12.516 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-high-path-1"/>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-5.323 11.27-5.323 11.27-.374.403-1.12.73-1.686.73H7.01c-.558 0-1.308-.333-1.675-.76C5.335 18.24 0 12.516 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-high-path-2"/>
                                            </defs>
                                            <g fill="none" fill-rule="evenodd">
                                                <mask id="sast-full-high-mask-1" fill="#fff">
                                                    <use xlink:href="#sast-full-high-path-1"/>
                                                </mask>
                                                <use fill="#D82D49" xlink:href="#sast-full-high-path-1"/>
                                                <path stroke="#BB1A34"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.342.48 2.682.488 3.924V7c0 2.52-.966 5.112-2.582 7.62-.57.884-1.18 1.694-1.79 2.41-.214.252-.41.472-.588.66-.104.113-.178.188-.215.224-.296.32-.91.586-1.334.586H7.01c-.42 0-1.028-.274-1.296-.585-.052-.056-.127-.14-.233-.26-.178-.202-.378-.436-.593-.697-.615-.747-1.23-1.564-1.804-2.422C2.097 13.06 1.34 11.62.906 10.284.64 9.462.5 8.697.5 8c0-.433.02-.895.056-1.38C.634 5.6.786 4.51.992 3.4c.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#BB1A34" mask="url(#sast-full-high-mask-1)"
                                                      d="M8 0h8v20H8z"/>
                                                <mask id="sast-full-high-mask-2" fill="#fff">
                                                    <use xlink:href="#sast-full-high-path-2"/>
                                                </mask>
                                                <path stroke="#BB1A34"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.342.48 2.682.488 3.924V7c0 2.52-.966 5.112-2.582 7.62-.57.884-1.18 1.694-1.79 2.41-.214.252-.41.472-.588.66-.104.113-.178.188-.215.224-.296.32-.91.586-1.334.586H7.01c-.42 0-1.028-.274-1.296-.585-.052-.056-.127-.14-.233-.26-.178-.202-.378-.436-.593-.697-.615-.747-1.23-1.564-1.804-2.422C2.097 13.06 1.34 11.62.906 10.284.64 9.462.5 8.697.5 8c0-.433.02-.895.056-1.38C.634 5.6.786 4.51.992 3.4c.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#FFF" mask="url(#sast-full-high-mask-2)"
                                                      d="M5 12h2V9.5h2V12h2V5H9v2.5H7V5H5"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="severity-title-name">High</div>
                                    <div class="severity-count">${dependencyResult.highVulnerability}</div>
                                </div>
                                <table id="osa-cve-table-high" class="cve-table sast-cve-table osa-cve-table-high">
                                    <tr>
                                        <th>Vulnerability</th>
                                        <th>Publish Date</th>
                                        <th>Library</th>
                                    </tr>
                                    <#list dependencyResult.dependencyHighCVEReportTable as cve>
                                        <#if cve.state =="NOT_EXPLOITABLE">
                                        <tr style="text-decoration: line-through">
                                        <#else>
                                        <tr>
                                        </#if>
                                        <td>${cve.name}</td>
                                        <td>${cve.publishDate}</td>
                                        <td>${cve.libraryName}</td>
                                    </tr>
                                    </#list>
                                </table>
                            </div>
                        </#if>

                        <#if dependencyResult.dependencyMediumCVEReportTable?size gt 0>
                            <div id="osa-cve-table-medium-container">
                                <div class="full-severity-title">
                                    <div class="severity-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg"
                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="20"
                                             viewBox="0 0 16 20"><title>Med</title>
                                            <defs>
                                                <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                      id="sast-full-med-path-1"/>
                                                <path d="M1 1.053L8 0l7 1.053s1 3.173 1 6.315c0 6.332-5.346 11.89-5.346 11.89-.36.41-1.097.742-1.663.742H7.01c-.558 0-1.3-.34-1.652-.77 0 0-5.358-6.056-5.358-10.81 0-3.338 1-7.367 1-7.367z"
                                                      id="sast-full-med-path-2"/>
                                            </defs>
                                            <g fill="none" fill-rule="evenodd">
                                                <mask id="sast-full-med-mask-1" fill="#fff">
                                                    <use xlink:href="#sast-full-med-path-1"/>
                                                </mask>
                                                <use fill="#FFAC00" xlink:href="#sast-full-med-path-1"/>
                                                <path stroke="#E49B16"
                                                      d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                <path fill="#D79201" mask="url(#sast-full-med-mask-1)"
                                                      d="M8 0h8v20H8z"/>
                                                <mask id="sast-full-med-mask-2" fill="#fff">
                                                    <use xlink:href="#sast-full-med-path-2"/>
                                                </mask>
                                                <path stroke="#D49100"
                                                      d="M1.41 1.497L8 .507l6.61.993c.02.067.04.144.064.228.114.425.23.898.337 1.407.3 1.418.48 2.83.49 4.143v.09c0 2.665-.972 5.404-2.6 8.06-.57.934-1.185 1.79-1.8 2.55-.213.264-.412.498-.59.698-.105.118-.18.198-.216.237-.282.32-.882.587-1.302.587H7.01c-.414 0-1.01-.277-1.266-.587-.05-.06-.126-.146-.233-.274-.18-.216-.38-.464-.594-.74-.62-.79-1.237-1.654-1.814-2.56-.982-1.55-1.74-3.06-2.18-4.463C.645 9.994.5 9.17.5 8.42c0-.457.02-.944.057-1.457.077-1.072.23-2.22.435-3.392.11-.614.224-1.197.34-1.73L1.41 1.5z"/>
                                                <path fill="#472F00" mask="url(#sast-full-med-mask-2)"
                                                      d="M4.28 12.632h1.9v-4.21l1.78 2.862H8L9.79 8.4v4.232h1.93v-7.37H9.67L8 8.117 6.33 5.263H4.28"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="severity-title-name">Medium</div>
                                    <div class="severity-count">${dependencyResult.mediumVulnerability}</div>
                                </div>
                                <table id="osa-cve-table-medium" class="cve-table sast-cve-table osa-cve-table-medium">
                                    <tr>
                                        <th>Vulnerability</th>
                                        <th>Publish Date</th>
                                        <th>Library</th>
                                    </tr>
                                    <#list dependencyResult.dependencyMediumCVEReportTable as cve>
                                        <#if cve.state =="NOT_EXPLOITABLE">
                                        <tr style="text-decoration: line-through">
                                        <#else>
                                        <tr>
                                        </#if>
                                        <td>${cve.name}</td>
                                        <td>${cve.publishDate}</td>
                                        <td>${cve.libraryName}</td>
                                    </tr>
                                    </#list>
                                </table>
                            </div>
                        </#if>

                        <#if dependencyResult.dependencyLowCVEReportTable?size gt 0>
                            <div id="osa-cve-table-low-container">
                                <div class="full-severity-title">
                                    <div class="severity-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg"
                                             xmlns:xlink="http://www.w3.org/1999/xlink" width="16" height="19"
                                             viewBox="0 0 16 19"><title>Low</title>
                                            <defs>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-low-path-1"/>
                                                <path d="M1 1l7-1 7 1s1 3.015 1 6c0 6.015-6 12-6 12H6S0 12.515 0 8c0-3.172 1-7 1-7z"
                                                      id="sast-full-low-path-2"/>
                                            </defs>
                                            <g fill="none" fill-rule="evenodd">
                                                <path d="M7.96 17.32L8 .015l-6.5 1s-.96 4.5-.96 8.75c1.272 4.602 5.968 9.25 5.968 9.25h.163l1.29-1.695z"
                                                      fill="#EDEFF5"/>
                                                <mask id="sast-full-low-mask-1" fill="#fff">
                                                    <use xlink:href="#sast-full-low-path-1"/>
                                                </mask>
                                                <use fill="#FFEB3B" xlink:href="#sast-full-low-path-1"/>
                                                <path stroke="#E4D200"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#DDCE00" mask="url(#sast-full-low-mask-1)"
                                                      d="M8-8h10v32H8z"/>
                                                <mask id="sast-full-low-mask-2" fill="#fff">
                                                    <use xlink:href="#sast-full-low-path-2"/>
                                                </mask>
                                                <path stroke="#E4D200"
                                                      d="M1.404 1.447L8 .505l6.616.945.06.205c.114.402.23.85.336 1.334.298 1.34.48 2.68.488 3.923V7c0 2.515-1.09 5.243-2.916 7.978-.644.966-1.335 1.863-2.026 2.667-.24.28-.465.53-.665.745-.04.04-.074.077-.105.11H6.222l-.105-.118c-.202-.23-.427-.492-.67-.785-.694-.837-1.388-1.744-2.035-2.687-.89-1.298-1.62-2.56-2.128-3.738C.772 9.982.5 8.912.5 8c0-.433.02-.895.056-1.38.078-1.02.23-2.11.436-3.22.108-.584.223-1.137.34-1.64.026-.118.05-.222.072-.313z"/>
                                                <path fill="#605900" mask="url(#sast-full-low-mask-2)"
                                                      d="M5.54 12h5.33v-1.7H7.48V5H5.54"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="severity-title-name">Low</div>
                                    <div class="severity-count">${dependencyResult.lowVulnerability}</div>
                                </div>
                                <table id="osa-cve-table-low" class="cve-table sast-cve-table osa-cve-table-low">
                                    <tr>
                                        <th>Vulnerability</th>
                                        <th>Publish Date</th>
                                        <th>Library</th>
                                    </tr>
                                    <#list dependencyResult.dependencyLowCVEReportTable as cve>
                                        <#if cve.state =="NOT_EXPLOITABLE">
                                        <tr style="text-decoration: line-through">
                                        <#else>
                                        <tr>
                                        </#if>
                                        <td>${cve.name}</td>
                                        <td>${cve.publishDate}</td>
                                        <td>${cve.libraryName}</td>
                                    </tr>
                                    </#list>
                                </table>
                            </div>
                        </#if>


                    </div>
                </div>
            </div>
        </#if>
    </#if>

    <#if (config.isOsaEnabled() || config.isAstScaEnabled()|| config.isSastEnabled()) &&  policyViolated>

        <#if policyViolatedCount gt 0>
        <div class="osa-full full-results-section">
        <div class="summary-table-row cxosa-full">
            <div class="title-column">
                <div class="summary-title">
                    <div class="sum1">Policy</div>
                    <div class="sum1">Management</div>
                </div>
                <div class="detailed-report">
                    <div class="full-downloads osa-downloads">
                        <div class="report-link">
                            <a href="${config.cxARMUrl}/cxarm/webclient/" class="html-report" id="arm-html-link">
                                <div class="link-to-result">
                                    <div class="results-link-icon link-icon">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="14"
                                             viewBox="0 0 12 14">
                                            <title>analyze</title>
                                            <g fill="none" fill-rule="evenodd">
                                                <circle stroke="#4A90E2" stroke-width="2" cx="5" cy="5" r="4"/>
                                                <path fill="#4A90E2"
                                                      d="M6.366 8.366l1.732-1 3.268 5.66-1.732 1z"/>
                                            </g>
                                        </svg>
                                    </div>
                                    <div class="link-text">Analyze Results</div>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="main-column">
            <div id="osa-policy-violations-container">
                <div class="full-severity-title">
                    <div class="severity-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="19px"
                             height="22px" viewBox="0 0 19 22" version="1.1">
                            <!-- Generator: Sketch 50.2 (55047) - http://www.bohemiancoding.com/sketch -->
                            <title>Policy violation</title>
                            <desc>Created with Sketch.</desc>
                            <defs>
                                <path d="M0,0 L15,0 L15,17 L0,17 L0,0 Z M3,1 L3,3 L12,3 L12,1 L3,1 Z M14,11 L14,11 C17.3137085,11 20,13.6862915 20,17 L20,17 C20,20.3137085 17.3137085,23 14,23 L14,23 C10.6862915,23 8,20.3137085 8,17 L8,17 C8,13.6862915 10.6862915,11 14,11 Z"
                                      id="path-1"/>
                            </defs>
                            <g id="Policy-mgmt" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <g id="Jenkins-eport" transform="translate(-525.000000, -2433.000000)">
                                    <g id="Osa-Full" transform="translate(272.000000, 1523.000000)">
                                        <g id="high-copy-2" transform="translate(249.000000, 909.000000)">
                                            <g id="TTL" transform="translate(4.000000, 0.000000)">
                                                <g id="Policy-mgmt" transform="translate(0.000000, 1.000000)">
                                                    <mask id="mask-2" fill="white">
                                                        <use xlink:href="#path-1"/>
                                                    </mask>
                                                    <g id="Mask"/>
                                                    <rect id="Rectangle-6" stroke="#373050" stroke-width="2"
                                                          mask="url(#mask-2)" x="1" y="2" width="13" height="14"/>
                                                    <rect id="Rectangle-2" fill="#373050" mask="url(#mask-2)" x="4"
                                                          y="6" width="7" height="1"/>
                                                    <rect id="Rectangle-2-Copy" fill="#373050" mask="url(#mask-2)" x="4"
                                                          y="9" width="7" height="1"/>
                                                    <rect id="Rectangle-2-Copy-2" fill="#373050" mask="url(#mask-2)"
                                                          x="4" y="12" width="7" height="1"/>
                                                    <rect id="Rectangle-2-Copy-3" fill="#373050" x="5" y="0" width="5"
                                                          height="3"/>
                                                    <g id="Alert_general_hover"
                                                       transform="translate(9.000000, 12.000000)">
                                                        <rect id="Rectangle-6-Copy" fill="#DA2946" x="0" y="0"
                                                              width="10" height="10" rx="5"/>
                                                        <rect id="Rectangle-7" fill="#FFFFFF" x="4" y="2" width="2"
                                                              height="4"/>
                                                        <rect id="Rectangle-7-Copy" fill="#FFFFFF" x="4" y="7" width="2"
                                                              height="1"/>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </g>
                                </g>
                            </g>
                        </svg>
                    </div>
                    <div class="severity-title-name">Violated ${policyLabel}</div>
                    <div class="severity-count">${policyViolatedCount}</div>
                </div>
                <table id="policy-violations-table" class="cve-table sast-cve-table osa-policy-violations">
                    <tr>
                        <th>Policy</th>
                        <th>Rule</th>
                        <th>Type</th>
                        <th># of Rule Violations</th>
                        <th>First Detection Date</th>
                    </tr>
                    <#if sast.sastPolicies?size gt 0>
                        <#list sast.sastPolicies as sastPoliciy>
                            <tr>
                                <td>${sastPoliciy.policyName}</td>
                                <td id="ruleName">${sastPoliciy.ruleName}</td>
                                <td>SAST</td>
                                <td>${sastPoliciy.violations?size}</td>
                                <td>${sastPoliciy.firstDetectionDate}</td>
                            </tr>
                        </#list>
                    </#if>
                    <#if osa.osaPolicies?size gt 0>
                        <#list osa.osaPolicies as osaPolicy>
                            <tr>
                                <td>${osaPolicy.policyName}</td>
                                <td id="ruleName">${osaPolicy.ruleName}</td>
                                <td>OSA</td>
                                <td>${osaPolicy.violations?size}</td>
                                <td>${osaPolicy.firstDetectionDate}</td>
                            </tr>
                        </#list>
                    </#if>
                </table>
            </div>
        </#if>

    </div>
    </div>
    </div>



    </#if>
    </div>
</div>

</body>
</html>