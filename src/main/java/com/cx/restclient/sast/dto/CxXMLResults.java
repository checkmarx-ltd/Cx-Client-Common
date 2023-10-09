package com.cx.restclient.sast.dto;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "query"
})
@XmlRootElement(name = "CxXMLResults")
public class CxXMLResults {

    @XmlElement(name = "Query")
    protected List<Query> query;
    @XmlAttribute(name = "InitiatorName")
    protected String initiatorName;
    @XmlAttribute(name = "Owner")
    protected String owner;
    @XmlAttribute(name = "ScanId")
    protected String scanId;
    @XmlAttribute(name = "ProjectId")
    protected String projectId;
    @XmlAttribute(name = "ProjectName")
    protected String projectName;
    @XmlAttribute(name = "TeamFullPathOnReportDate")
    protected String teamFullPathOnReportDate;
    @XmlAttribute(name = "DeepLink")
    @XmlSchemaType(name = "anyURI")
    protected String deepLink;
    @XmlAttribute(name = "ScanStart")
    protected String scanStart;
    @XmlAttribute(name = "Preset")
    protected String preset;
    @XmlAttribute(name = "ScanTime")
    protected String scanTime;
    @XmlAttribute(name = "LinesOfCodeScanned")
    protected String linesOfCodeScanned;
    @XmlAttribute(name = "FilesScanned")
    protected String filesScanned;
    @XmlAttribute(name = "ReportCreationTime")
    protected String reportCreationTime;
    @XmlAttribute(name = "Team")
    protected String team;
    @XmlAttribute(name = "CheckmarxVersion")
    protected String checkmarxVersion;
    @XmlAttribute(name = "ScanComments")
    protected String scanComments;
    @XmlAttribute(name = "ScanType")
    protected String scanType;
    @XmlAttribute(name = "SourceOrigin")
    protected String sourceOrigin;
    @XmlAttribute(name = "Visibility")
    protected String visibility;

    public List<Query> getQuery() {
        if (query == null) {
            query = new ArrayList<Query>();
        }
        return this.query;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String value) {
        this.initiatorName = value;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String value) {
        this.owner = value;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String value) {
        this.scanId = value;
    }


    public String getProjectId() {
        return projectId;
    }


    public void setProjectId(String value) {
        this.projectId = value;
    }


    public String getProjectName() {
        return projectName;
    }


    public void setProjectName(String value) {
        this.projectName = value;
    }


    public String getTeamFullPathOnReportDate() {
        return teamFullPathOnReportDate;
    }


    public void setTeamFullPathOnReportDate(String value) {
        this.teamFullPathOnReportDate = value;
    }


    public String getDeepLink() {
        return deepLink;
    }


    public void setDeepLink(String value) {
        this.deepLink = value;
    }


    public String getScanStart() {
        return scanStart;
    }


    public void setScanStart(String value) {
        this.scanStart = value;
    }


    public String getPreset() {
        return preset;
    }


    public void setPreset(String value) {
        this.preset = value;
    }


    public String getScanTime() {
        return scanTime;
    }


    public void setScanTime(String value) {
        this.scanTime = value;
    }


    public String getLinesOfCodeScanned() {
        return linesOfCodeScanned;
    }


    public void setLinesOfCodeScanned(String value) {
        this.linesOfCodeScanned = value;
    }


    public String getFilesScanned() {
        return filesScanned;
    }


    public void setFilesScanned(String value) {
        this.filesScanned = value;
    }


    public String getReportCreationTime() {
        return reportCreationTime;
    }


    public void setReportCreationTime(String value) {
        this.reportCreationTime = value;
    }


    public String getTeam() {
        return team;
    }


    public void setTeam(String value) {
        this.team = value;
    }


    public String getCheckmarxVersion() {
        return checkmarxVersion;
    }


    public void setCheckmarxVersion(String value) {
        this.checkmarxVersion = value;
    }


    public String getScanComments() {
        return scanComments;
    }


    public void setScanComments(String value) {
        this.scanComments = value;
    }


    public String getScanType() {
        return scanType;
    }


    public void setScanType(String value) {
        this.scanType = value;
    }


    public String getSourceOrigin() {
        return sourceOrigin;
    }


    public void setSourceOrigin(String value) {
        this.sourceOrigin = value;
    }


    public String getVisibility() {
        return visibility;
    }


    public void setVisibility(String value) {
        this.visibility = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "result"
    })
    public static class Query implements Serializable {
        @Override
        public String toString() {
            return "{" +
                    "'name': '" + name +
                    "', 'severity': '" + severity +
                    "', 'resultLength': '" + result.size() +
                    "'}";
        }

        @XmlElement(name = "Result")
        protected List<Result> result;
        @XmlAttribute(name = "id")
        protected String id;
        @XmlAttribute(name = "categories")
        protected String categories;
        @XmlAttribute(name = "cweId")
        protected String cweId;
        @XmlAttribute(name = "name")
        protected String name;
        @XmlAttribute(name = "group")
        protected String group;
        @XmlAttribute(name = "Severity")
        protected String severity;
        @XmlAttribute(name = "Language")
        protected String language;
        @XmlAttribute(name = "LanguageHash")
        protected String languageHash;
        @XmlAttribute(name = "LanguageChangeDate")
        @XmlSchemaType(name = "dateTime")
        protected String languageChangeDate;
        @XmlAttribute(name = "SeverityIndex")
        protected String severityIndex;
        @XmlAttribute(name = "QueryPath")
        protected String queryPath;
        @XmlAttribute(name = "QueryVersionCode")
        protected String queryVersionCode;


        public List<Result> getResult() {
            if (result == null) {
                result = new ArrayList<Result>();
            }
            return this.result;
        }


        public String getId() {
            return id;
        }


        public void setId(String value) {
            this.id = value;
        }


        public String getCategories() {
            return categories;
        }


        public void setCategories(String value) {
            this.categories = value;
        }


        public String getCweId() {
            return cweId;
        }


        public void setCweId(String value) {
            this.cweId = value;
        }


        public String getName() {
            return name;
        }


        public void setName(String value) {
            this.name = value;
        }


        public String getGroup() {
            return group;
        }


        public void setGroup(String value) {
            this.group = value;
        }


        public String getSeverity() {
            return severity;
        }


        public void setSeverity(String value) {
            this.severity = value;
        }


        public String getLanguage() {
            return language;
        }


        public void setLanguage(String value) {
            this.language = value;
        }


        public String getLanguageHash() {
            return languageHash;
        }


        public void setLanguageHash(String value) {
            this.languageHash = value;
        }


        public String getLanguageChangeDate() {
            return languageChangeDate;
        }


        public void setLanguageChangeDate(String value) {
            this.languageChangeDate = value;
        }


        public String getSeverityIndex() {
            return severityIndex;
        }


        public void setSeverityIndex(String value) {
            this.severityIndex = value;
        }


        public String getQueryPath() {
            return queryPath;
        }


        public void setQueryPath(String value) {
            this.queryPath = value;
        }


        public String getQueryVersionCode() {
            return queryVersionCode;
        }


        public void setQueryVersionCode(String value) {
            this.queryVersionCode = value;
        }


        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "path"
        })
        public static class Result implements Serializable {

            @XmlElement(name = "Path", required = true)
            protected Path path;
            @XmlAttribute(name = "NodeId")
            protected String nodeId;
            @XmlAttribute(name = "FileName")
            protected String fileName;
            @XmlAttribute(name = "Status")
            protected String status;
            @XmlAttribute(name = "Line")
            protected String line;
            @XmlAttribute(name = "Column")
            protected String column;
            @XmlAttribute(name = "FalsePositive")
            protected String falsePositive;
            @XmlAttribute(name = "Severity")
            protected String severity;
            @XmlAttribute(name = "AssignToUser")
            protected String assignToUser;
            @XmlAttribute(name = "state")
            protected String state;
            @XmlAttribute(name = "Remark")
            protected String remark;
            @XmlAttribute(name = "DeepLink")
            @XmlSchemaType(name = "anyURI")
            protected String deepLink;
            @XmlAttribute(name = "SeverityIndex")
            protected String severityIndex;


            public Path getPath() {
                return path;
            }


            public void setPath(Path value) {
                this.path = value;
            }


            public String getNodeId() {
                return nodeId;
            }


            public void setNodeId(String value) {
                this.nodeId = value;
            }


            public String getFileName() {
                return fileName;
            }


            public void setFileName(String value) {
                this.fileName = value;
            }


            public String getStatus() {
                return status;
            }


            public void setStatus(String value) {
                this.status = value;
            }


            public String getLine() {
                return line;
            }


            public void setLine(String value) {
                this.line = value;
            }


            public String getColumn() {
                return column;
            }


            public void setColumn(String value) {
                this.column = value;
            }


            public String getFalsePositive() {
                return falsePositive;
            }


            public void setFalsePositive(String value) {
                this.falsePositive = value;
            }


            public String getSeverity() {
                return severity;
            }


            public void setSeverity(String value) {
                this.severity = value;
            }


            public String getAssignToUser() {
                return assignToUser;
            }


            public void setAssignToUser(String value) {
                this.assignToUser = value;
            }


            public String getState() {
                return state;
            }


            public void setState(String value) {
                this.state = value;
            }


            public String getRemark() {
                return remark;
            }


            public void setRemark(String value) {
                this.remark = value;
            }


            public String getDeepLink() {
                return deepLink;
            }


            public void setDeepLink(String value) {
                this.deepLink = value;
            }


            public String getSeverityIndex() {
                return severityIndex;
            }


            public void setSeverityIndex(String value) {
                this.severityIndex = value;
            }


            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "pathNode"
            })
            public static class Path implements Serializable {

                @XmlElement(name = "PathNode")
                protected List<PathNode> pathNode;
                @XmlAttribute(name = "ResultId")
                protected String resultId;
                @XmlAttribute(name = "PathId")
                protected String pathId;
                @XmlAttribute(name = "SimilarityId")
                protected String similarityId;


                public List<PathNode> getPathNode() {
                    if (pathNode == null) {
                        pathNode = new ArrayList<PathNode>();
                    }
                    return this.pathNode;
                }


                public String getResultId() {
                    return resultId;
                }


                public void setResultId(String value) {
                    this.resultId = value;
                }


                public String getPathId() {
                    return pathId;
                }


                public void setPathId(String value) {
                    this.pathId = value;
                }


                public String getSimilarityId() {
                    return similarityId;
                }


                public void setSimilarityId(String value) {
                    this.similarityId = value;
                }


                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                        "fileName",
                        "line",
                        "column",
                        "nodeId",
                        "name",
                        "type",
                        "length",
                        "snippet"
                })
                public static class PathNode implements Serializable {

                    @XmlElement(name = "FileName", required = true)
                    protected String fileName;
                    @XmlElement(name = "Line")
                    protected String line;
                    @XmlElement(name = "Column")
                    protected String column;
                    @XmlElement(name = "NodeId")
                    protected String nodeId;
                    @XmlElement(name = "Name", required = true)
                    protected String name;
                    @XmlElement(name = "Type", required = true)
                    protected String type;
                    @XmlElement(name = "Length")
                    protected String length;
                    @XmlElement(name = "Snippet", required = true)
                    protected Snippet snippet;


                    public String getFileName() {
                        return fileName;
                    }


                    public void setFileName(String value) {
                        this.fileName = value;
                    }


                    public String getLine() {
                        return line;
                    }


                    public void setLine(String value) {
                        this.line = value;
                    }


                    public String getColumn() {
                        return column;
                    }


                    public void setColumn(String value) {
                        this.column = value;
                    }


                    public String getNodeId() {
                        return nodeId;
                    }


                    public void setNodeId(String value) {
                        this.nodeId = value;
                    }


                    public String getName() {
                        return name;
                    }


                    public void setName(String value) {
                        this.name = value;
                    }


                    public String getType() {
                        return type;
                    }


                    public void setType(String value) {
                        this.type = value;
                    }


                    public String getLength() {
                        return length;
                    }


                    public void setLength(String value) {
                        this.length = value;
                    }


                    public Snippet getSnippet() {
                        return snippet;
                    }


                    public void setSnippet(Snippet value) {
                        this.snippet = value;
                    }


                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                            "line"
                    })
                    public static class Snippet implements Serializable {

                        @XmlElement(name = "Line", required = true)
                        protected Line line;


                        public Line getLine() {
                            return line;
                        }


                        public void setLine(Line value) {
                            this.line = value;
                        }


                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                                "number",
                                "code"
                        })
                        public static class Line implements Serializable {

                            @XmlElement(name = "Number")
                            protected String number;
                            @XmlElement(name = "Code", required = true)
                            protected String code;


                            public String getNumber() {
                                return number;
                            }

                            public void setNumber(String value) {
                                this.number = value;
                            }


                            public String getCode() {
                                return code;
                            }


                            public void setCode(String value) {
                                this.code = value;
                            }

                        }


                    }

                }

            }

        }

    }

}
