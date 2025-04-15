package org.morts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morts.dto.GameInfoDTO;
import org.morts.dto.GameScorekeepingDTO;
import org.morts.lambdas.CreateScorekeepingGameLambda;

import java.sql.SQLException;

public class Main {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws SQLException, ClassNotFoundException, JsonProcessingException {

        String gameScorekeepingDTOJson = "{\n" +
                "\t\"gameInfo\" : {\n" +
                "\t\t\t\"date\": \"2025-05-01T23:15:51.796Z\",\n" +
                "\t\t\t\"field\": \"Bossen #2\",\n" +
                "\t\t\t\"temperature\": 75,\n" +
                "\t\t\t\"weatherConditions\": [\n" +
                "\t\t\t\t\t\"SUNNY\"\n" +
                "\t\t\t],\n" +
                "\t\t\t\"opponent\": {\n" +
                "\t\t\t\t\t\"id\": 1,\n" +
                "\t\t\t\t\t\"teamName\": \"Dads on Vacation\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"runsAgainst\": 5,\n" +
                "\t\t\t\"runsFor\": 12,\n" +
                "\t\t\t\"gameNotes\": \"These are the game notes.\"\n" +
                "\t},\n" +
                "\t\"season\" : {\n" +
                "\t\t\t\"id\": 1,\n" +
                "\t\t\t\"year\": 2021,\n" +
                "\t\t\t\"session\": \"Spring\"\n" +
                "},\n" +
                "\t\"innings\" : [\n" +
                "    {\n" +
                "        \"inning\": 1,\n" +
                "        \"atBats\": [\n" +
                "            {\n" +
                "                \"inningIndex\": 1,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 3,\n" +
                "                    \"firstName\": \"Evan\",\n" +
                "                    \"lastName\": \"Wenzinger\",\n" +
                "                    \"height\": \"6'4\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Double\",\n" +
                "                \"scoring\": \"2B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": null,\n" +
                "                    \"second\": {\n" +
                "                        \"id\": 3,\n" +
                "                        \"firstName\": \"Evan\",\n" +
                "                        \"lastName\": \"Wenzinger\",\n" +
                "                        \"height\": \"6'4\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 2,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 2,\n" +
                "                    \"firstName\": \"Levi\",\n" +
                "                    \"lastName\": \"Schwartzberg\",\n" +
                "                    \"height\": \"5'11\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 3,\n" +
                "                        \"firstName\": \"Evan\",\n" +
                "                        \"lastName\": \"Wenzinger\",\n" +
                "                        \"height\": \"6'4\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 3,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 5,\n" +
                "                    \"firstName\": \"Alex\",\n" +
                "                    \"lastName\": \"Simmons\",\n" +
                "                    \"height\": \"5'10\",\n" +
                "                    \"weight\": 175,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 5,\n" +
                "                        \"firstName\": \"Alex\",\n" +
                "                        \"lastName\": \"Simmons\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 4,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 4,\n" +
                "                    \"firstName\": \"Mitch\",\n" +
                "                    \"lastName\": \"Ranke\",\n" +
                "                    \"height\": \"6'5\",\n" +
                "                    \"weight\": 230,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Homerun\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 4,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Ranke\",\n" +
                "                        \"height\": \"6'5\",\n" +
                "                        \"weight\": 230,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 5,\n" +
                "                        \"firstName\": \"Alex\",\n" +
                "                        \"lastName\": \"Simmons\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"HR\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": null,\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 5,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 8,\n" +
                "                    \"firstName\": \"Brett\",\n" +
                "                    \"lastName\": \"Shepley\",\n" +
                "                    \"height\": \"6'2\",\n" +
                "                    \"weight\": 210,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 8,\n" +
                "                        \"firstName\": \"Brett\",\n" +
                "                        \"lastName\": \"Shepley\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 210,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"F8\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": null,\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 6,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 7,\n" +
                "                    \"firstName\": \"Jake\",\n" +
                "                    \"lastName\": \"Peterson\",\n" +
                "                    \"height\": \"6'3\",\n" +
                "                    \"weight\": 190,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 7,\n" +
                "                        \"firstName\": \"Jake\",\n" +
                "                        \"lastName\": \"Peterson\",\n" +
                "                        \"height\": \"6'3\",\n" +
                "                        \"weight\": 190,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"L5\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": null,\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 7,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 10,\n" +
                "                    \"firstName\": \"Andrew\",\n" +
                "                    \"lastName\": \"Lindeman\",\n" +
                "                    \"height\": \"6'1\",\n" +
                "                    \"weight\": 210,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 10,\n" +
                "                        \"firstName\": \"Andrew\",\n" +
                "                        \"lastName\": \"Lindeman\",\n" +
                "                        \"height\": \"6'1\",\n" +
                "                        \"weight\": 210,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 8,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 11,\n" +
                "                    \"firstName\": \"Jack\",\n" +
                "                    \"lastName\": \"Voeller\",\n" +
                "                    \"height\": \"6'2\",\n" +
                "                    \"weight\": 200,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 10,\n" +
                "                        \"firstName\": \"Andrew\",\n" +
                "                        \"lastName\": \"Lindeman\",\n" +
                "                        \"height\": \"6'1\",\n" +
                "                        \"weight\": 210,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"6-4\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 11,\n" +
                "                        \"firstName\": \"Jack\",\n" +
                "                        \"lastName\": \"Voeller\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 200,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"inning\": 2,\n" +
                "        \"atBats\": [\n" +
                "            {\n" +
                "                \"inningIndex\": 1,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 9,\n" +
                "                    \"firstName\": \"Mitch\",\n" +
                "                    \"lastName\": \"Stafford\",\n" +
                "                    \"height\": \"5'10\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 9,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Stafford\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 2,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 6,\n" +
                "                    \"firstName\": \"Elliott\",\n" +
                "                    \"lastName\": \"Imhoff\",\n" +
                "                    \"height\": \"5'10\",\n" +
                "                    \"weight\": 175,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 9,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Stafford\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"6-4\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 6,\n" +
                "                        \"firstName\": \"Elliott\",\n" +
                "                        \"lastName\": \"Imhoff\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 3,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 3,\n" +
                "                    \"firstName\": \"Evan\",\n" +
                "                    \"lastName\": \"Wenzinger\",\n" +
                "                    \"height\": \"6'4\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 3,\n" +
                "                        \"firstName\": \"Evan\",\n" +
                "                        \"lastName\": \"Wenzinger\",\n" +
                "                        \"height\": \"6'4\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"F8\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 6,\n" +
                "                        \"firstName\": \"Elliott\",\n" +
                "                        \"lastName\": \"Imhoff\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 4,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 2,\n" +
                "                    \"firstName\": \"Levi\",\n" +
                "                    \"lastName\": \"Schwartzberg\",\n" +
                "                    \"height\": \"5'11\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Double\",\n" +
                "                \"scoring\": \"2B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": null,\n" +
                "                    \"second\": {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"third\": {\n" +
                "                        \"id\": 6,\n" +
                "                        \"firstName\": \"Elliott\",\n" +
                "                        \"lastName\": \"Imhoff\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 5,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 5,\n" +
                "                    \"firstName\": \"Alex\",\n" +
                "                    \"lastName\": \"Simmons\",\n" +
                "                    \"height\": \"5'10\",\n" +
                "                    \"weight\": 175,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 6,\n" +
                "                        \"firstName\": \"Elliott\",\n" +
                "                        \"lastName\": \"Imhoff\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 5,\n" +
                "                        \"firstName\": \"Alex\",\n" +
                "                        \"lastName\": \"Simmons\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 6,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 4,\n" +
                "                    \"firstName\": \"Mitch\",\n" +
                "                    \"lastName\": \"Ranke\",\n" +
                "                    \"height\": \"6'5\",\n" +
                "                    \"weight\": 230,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 4,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Ranke\",\n" +
                "                        \"height\": \"6'5\",\n" +
                "                        \"weight\": 230,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": {\n" +
                "                        \"id\": 5,\n" +
                "                        \"firstName\": \"Alex\",\n" +
                "                        \"lastName\": \"Simmons\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 7,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 8,\n" +
                "                    \"firstName\": \"Brett\",\n" +
                "                    \"lastName\": \"Shepley\",\n" +
                "                    \"height\": \"6'2\",\n" +
                "                    \"weight\": 210,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Double\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 5,\n" +
                "                        \"firstName\": \"Alex\",\n" +
                "                        \"lastName\": \"Simmons\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"2B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": null,\n" +
                "                    \"second\": {\n" +
                "                        \"id\": 8,\n" +
                "                        \"firstName\": \"Brett\",\n" +
                "                        \"lastName\": \"Shepley\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 210,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"third\": {\n" +
                "                        \"id\": 4,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Ranke\",\n" +
                "                        \"height\": \"6'5\",\n" +
                "                        \"weight\": 230,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 8,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 7,\n" +
                "                    \"firstName\": \"Jake\",\n" +
                "                    \"lastName\": \"Peterson\",\n" +
                "                    \"height\": \"6'3\",\n" +
                "                    \"weight\": 190,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 8,\n" +
                "                        \"firstName\": \"Brett\",\n" +
                "                        \"lastName\": \"Shepley\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 210,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 4,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Ranke\",\n" +
                "                        \"height\": \"6'5\",\n" +
                "                        \"weight\": 230,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 7,\n" +
                "                        \"firstName\": \"Jake\",\n" +
                "                        \"lastName\": \"Peterson\",\n" +
                "                        \"height\": \"6'3\",\n" +
                "                        \"weight\": 190,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 9,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 10,\n" +
                "                    \"firstName\": \"Andrew\",\n" +
                "                    \"lastName\": \"Lindeman\",\n" +
                "                    \"height\": \"6'1\",\n" +
                "                    \"weight\": 210,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"scoring\": \"F8\",\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 10,\n" +
                "                        \"firstName\": \"Andrew\",\n" +
                "                        \"lastName\": \"Lindeman\",\n" +
                "                        \"height\": \"6'1\",\n" +
                "                        \"weight\": 210,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 7,\n" +
                "                        \"firstName\": \"Jake\",\n" +
                "                        \"lastName\": \"Peterson\",\n" +
                "                        \"height\": \"6'3\",\n" +
                "                        \"weight\": 190,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"inning\": 3,\n" +
                "        \"atBats\": [\n" +
                "            {\n" +
                "                \"inningIndex\": 1,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 11,\n" +
                "                    \"firstName\": \"Jack\",\n" +
                "                    \"lastName\": \"Voeller\",\n" +
                "                    \"height\": \"6'2\",\n" +
                "                    \"weight\": 200,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 11,\n" +
                "                        \"firstName\": \"Jack\",\n" +
                "                        \"lastName\": \"Voeller\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 200,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 2,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 9,\n" +
                "                    \"firstName\": \"Mitch\",\n" +
                "                    \"lastName\": \"Stafford\",\n" +
                "                    \"height\": \"5'10\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 9,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Stafford\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"L9\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 11,\n" +
                "                        \"firstName\": \"Jack\",\n" +
                "                        \"lastName\": \"Voeller\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 200,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 3,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 6,\n" +
                "                    \"firstName\": \"Elliott\",\n" +
                "                    \"lastName\": \"Imhoff\",\n" +
                "                    \"height\": \"5'10\",\n" +
                "                    \"weight\": 175,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 6,\n" +
                "                        \"firstName\": \"Elliott\",\n" +
                "                        \"lastName\": \"Imhoff\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": {\n" +
                "                        \"id\": 11,\n" +
                "                        \"firstName\": \"Jack\",\n" +
                "                        \"lastName\": \"Voeller\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 200,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": [],\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 4,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 3,\n" +
                "                    \"firstName\": \"Evan\",\n" +
                "                    \"lastName\": \"Wenzinger\",\n" +
                "                    \"height\": \"6'4\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 11,\n" +
                "                        \"firstName\": \"Jack\",\n" +
                "                        \"lastName\": \"Voeller\",\n" +
                "                        \"height\": \"6'2\",\n" +
                "                        \"weight\": 200,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 3,\n" +
                "                        \"firstName\": \"Evan\",\n" +
                "                        \"lastName\": \"Wenzinger\",\n" +
                "                        \"height\": \"6'4\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": {\n" +
                "                        \"id\": 6,\n" +
                "                        \"firstName\": \"Elliott\",\n" +
                "                        \"lastName\": \"Imhoff\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 5,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 2,\n" +
                "                    \"firstName\": \"Levi\",\n" +
                "                    \"lastName\": \"Schwartzberg\",\n" +
                "                    \"height\": \"5'11\",\n" +
                "                    \"weight\": 180,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Single\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 6,\n" +
                "                        \"firstName\": \"Elliott\",\n" +
                "                        \"lastName\": \"Imhoff\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"1B\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": {\n" +
                "                        \"id\": 3,\n" +
                "                        \"firstName\": \"Evan\",\n" +
                "                        \"lastName\": \"Wenzinger\",\n" +
                "                        \"height\": \"6'4\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"outs\": []\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 6,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 5,\n" +
                "                    \"firstName\": \"Alex\",\n" +
                "                    \"lastName\": \"Simmons\",\n" +
                "                    \"height\": \"5'10\",\n" +
                "                    \"weight\": 175,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"runs\": [\n" +
                "                    {\n" +
                "                        \"id\": 3,\n" +
                "                        \"firstName\": \"Evan\",\n" +
                "                        \"lastName\": \"Wenzinger\",\n" +
                "                        \"height\": \"6'4\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 5,\n" +
                "                        \"firstName\": \"Alex\",\n" +
                "                        \"lastName\": \"Simmons\",\n" +
                "                        \"height\": \"5'10\",\n" +
                "                        \"weight\": 175,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"SAC-7\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"inningIndex\": 7,\n" +
                "                \"player\": {\n" +
                "                    \"id\": 4,\n" +
                "                    \"firstName\": \"Mitch\",\n" +
                "                    \"lastName\": \"Ranke\",\n" +
                "                    \"height\": \"6'5\",\n" +
                "                    \"weight\": 230,\n" +
                "                    \"batHand\": \"Right\",\n" +
                "                    \"throwHand\": \"Right\"\n" +
                "                },\n" +
                "                \"result\": \"Out(s)\",\n" +
                "                \"outs\": [\n" +
                "                    {\n" +
                "                        \"id\": 4,\n" +
                "                        \"firstName\": \"Mitch\",\n" +
                "                        \"lastName\": \"Ranke\",\n" +
                "                        \"height\": \"6'5\",\n" +
                "                        \"weight\": 230,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"scoring\": \"F8\",\n" +
                "                \"baserunners\": {\n" +
                "                    \"first\": {\n" +
                "                        \"id\": 2,\n" +
                "                        \"firstName\": \"Levi\",\n" +
                "                        \"lastName\": \"Schwartzberg\",\n" +
                "                        \"height\": \"5'11\",\n" +
                "                        \"weight\": 180,\n" +
                "                        \"batHand\": \"Right\",\n" +
                "                        \"throwHand\": \"Right\"\n" +
                "                    },\n" +
                "                    \"second\": null,\n" +
                "                    \"third\": null\n" +
                "                },\n" +
                "                \"runs\": []\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"inning\": 4,\n" +
                "        \"atBats\": []\n" +
                "    }\n" +
                "]\n" +
                "}";

        GameScorekeepingDTO gameScorekeepingDTO = objectMapper.readValue(gameScorekeepingDTOJson, GameScorekeepingDTO.class);

        CreateScorekeepingGameLambda createScorekeepingGameLambda = new CreateScorekeepingGameLambda();

        GameInfoDTO createdGameInfoDTO = createScorekeepingGameLambda.createGameInfo(gameScorekeepingDTO.getGameInfo(), gameScorekeepingDTO.getSeason().getId());
        createScorekeepingGameLambda.createInnings(gameScorekeepingDTO.getInnings(), createdGameInfoDTO.getGameInfoId());
    }
}