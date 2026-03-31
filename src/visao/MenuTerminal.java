package visao;

import dao.AlunoDAO;
import dao.ExercicioDAO;
import dao.FichaDAO;
import model.Aluno;
import model.Exercicio;
import model.Ficha;
import model.ItemFicha;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class MenuTerminal {
    private Scanner scanner = new Scanner(System.in);
    private AlunoDAO alunoDAO = new AlunoDAO();
    private ExercicioDAO exercicioDAO = new ExercicioDAO();
    private FichaDAO fichaDAO = new FichaDAO();

    public void iniciar() {
        while (true) {
            System.out.println("\n=== SISTEMA ACADEMIA ===");
            System.out.println("1. Acesso Professor");
            System.out.println("2. Acesso Aluno (Visualização)");
            System.out.println("0. Sair");
            System.out.print("Escolha a opção: ");

            try {
                int op = Integer.parseInt(scanner.nextLine());
                if (op == 1) menuProfessor();
                else if (op == 2) menuAluno();
                else if (op == 0) {
                    System.out.println("Encerrando sistema...");
                    break;
                } else {
                    System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }

    private void menuProfessor() {
        while (true) {
            System.out.println("\n--- PAINEL DO PROFESSOR ---");
            System.out.println("1. Cadastrar Aluno");
            System.out.println("2. Cadastrar Exercício");
            System.out.println("3. Montar Nova Ficha (Máx 6 dias de treino)");
            System.out.println("4. Listar Alunos (Controle de Pagamento)");
            System.out.println("5. Listar Fichas Criadas (Resumo)");
            System.out.println("6. Ver Ficha Completa de um Aluno");
            System.out.println("7. Renovar Mensalidade de um Aluno");
            System.out.println("8. Excluir Aluno do Sistema");
            System.out.println("9. Excluir Exercício do Sistema");
            System.out.println("10. Listar Todos os Exercícios");
            System.out.println("11. Excluir Ficha de Treino");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            try {
                int op = Integer.parseInt(scanner.nextLine());

                if (op == 1) {
                    System.out.print("Nome do Aluno: ");
                    String nome = scanner.nextLine();
                    System.out.print("CPF: ");
                    String cpf = scanner.nextLine();
                    System.out.print("Altura (Em cm, ex: 180): ");
                    int altura = Integer.parseInt(scanner.nextLine());

                    java.time.LocalDate vencimento = java.time.LocalDate.now().plusMonths(1);
                    Aluno novoAluno = new Aluno(nome, cpf, altura, vencimento);
                    novoAluno.setCpf(cpf);
                    novoAluno.setAltura(altura);
                    alunoDAO.salvar(novoAluno);
                }
                else if (op == 2) {
                    System.out.print("Nome Técnico: "); String tec = scanner.nextLine();
                    System.out.print("Nome Popular: "); String pop = scanner.nextLine();
                    System.out.print("Grupo Muscular: "); String corpo = scanner.nextLine();
                    exercicioDAO.salvar(new Exercicio(tec, pop, corpo));
                }
                else if (op == 3) {
                    montarFichaFluxo();
                }
                else if (op == 4) {
                    List<Aluno> alunos = alunoDAO.listarTodos();
                    System.out.println("\n--- LISTA DE ALUNOS E MENSALIDADES ---");
                    for (Aluno a : alunos) {
                        String status = a.isPagamentoAtrasado() ? "ATRASADO ❌" : "EM DIA ✅";

                        // ALTERAÇÃO AQUI: Trocamos o "ID: %d" por "CPF: %s" e o a.getId() por a.getCpf()
                        System.out.printf("CPF: %s | Nome: %s | Vence em: %s | Status: %s\n",
                                a.getCpf(), a.getNome(), a.getDataVencimento(), status);
                    }
                }
                else if (op == 5) {
                    List<Ficha> todas = fichaDAO.listarTodasResumo();
                    System.out.println("\n--- FICHAS RECENTES (RESUMO) ---");
                    if (todas.isEmpty()) {
                        System.out.println("Nenhuma ficha foi montada ainda.");
                    } else {
                        for (Ficha f : todas) {
                            System.out.printf("Ficha ID: %d | Aluno: %s | Semana: %s\n", f.getId(), f.getNomeAlunoTemporario(), f.getSemana());
                        }
                    }
                }
                else if (op == 6) {
                    System.out.print("\nDigite o CPF do Aluno para ver a ficha completa: ");
                    String cpfBusca = scanner.nextLine();
                    Aluno alunoEncontrado = alunoDAO.buscarPorCpf(cpfBusca);

                    if (alunoEncontrado != null) {
                        Ficha fichaCompleta = fichaDAO.buscarFichaRecentePorAluno(alunoEncontrado.getId());
                        mostrarFichaFormatada(fichaCompleta);
                    } else {
                        System.out.println("❌ Nenhum aluno encontrado com o CPF informado.");
                    }
                }
                else if (op == 7) {
                    System.out.print("\nDigite o CPF do Aluno que está pagando a mensalidade: ");
                    String cpfRenovar = scanner.nextLine();
                    Aluno alunoEncontrado = alunoDAO.buscarPorCpf(cpfRenovar);

                    if (alunoEncontrado != null) {
                        alunoDAO.renovarMensalidade(alunoEncontrado.getId());
                    } else {
                        System.out.println("❌ Nenhum aluno encontrado com o CPF informado.");
                    }
                }
                else if (op == 8) {
                    System.out.print("\nDigite o ID do Aluno que deseja EXCLUIR: ");
                    int idExcluir = Integer.parseInt(scanner.nextLine());
                    System.out.print("⚠️ TEM CERTEZA? Isso apagará o aluno e todas as fichas dele! (s/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("s")) {
                        alunoDAO.excluir(idExcluir);
                    } else {
                        System.out.println("Exclusão cancelada.");
                    }
                }
                else if (op == 9) {
                    System.out.println("\n--- LISTA DE EXERCÍCIOS ---");
                    List<Exercicio> listaEx = exercicioDAO.listarTodos();
                    if (listaEx.isEmpty()) {
                        System.out.println("Nenhum exercício cadastrado.");
                    } else {
                        for (Exercicio ex : listaEx) {
                            System.out.printf("ID: %d | %s (%s)\n", ex.getId(), ex.getNomePopular(), ex.getParteCorpo());
                        }
                        System.out.print("\nDigite o ID do Exercício que deseja EXCLUIR: ");
                        int idExcluir = Integer.parseInt(scanner.nextLine());
                        System.out.print("⚠️ TEM CERTEZA? (s/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("s")) {
                            exercicioDAO.excluir(idExcluir);
                        } else {
                            System.out.println("Exclusão cancelada.");
                        }
                    }
                }
                else if (op == 10) {
                    System.out.println("\n--- CATÁLOGO DE EXERCÍCIOS ---");
                    List<Exercicio> listaEx = exercicioDAO.listarTodos();
                    if (listaEx.isEmpty()) {
                        System.out.println("Nenhum exercício cadastrado no sistema.");
                    } else {
                        for (Exercicio ex : listaEx) {
                            System.out.printf("ID: %d | Técnico: %s | Popular: %s | Músculo: %s\n",
                                    ex.getId(), ex.getNomeTecnico(), ex.getNomePopular(), ex.getParteCorpo());
                        }
                    }
                }
                else if (op == 11) {
                    System.out.println("\n--- EXCLUIR FICHA DE TREINO ---");

                    // Lista as fichas para o professor saber o ID
                    List<Ficha> todas = fichaDAO.listarTodasResumo();
                    if (todas.isEmpty()) {
                        System.out.println("Nenhuma ficha cadastrada no sistema para excluir.");
                    } else {
                        for (Ficha f : todas) {
                            System.out.printf("Ficha ID: %d | Aluno: %s | Semana: %s\n",
                                    f.getId(), f.getNomeAlunoTemporario(), f.getSemana());
                        }

                        System.out.print("\nDigite o ID da Ficha que deseja EXCLUIR: ");
                        int idExcluir = Integer.parseInt(scanner.nextLine());

                        System.out.print("⚠️ TEM CERTEZA? Isso apagará a ficha e todos os exercícios dentro dela! (s/n): ");
                        if (scanner.nextLine().equalsIgnoreCase("s")) {
                            fichaDAO.excluir(idExcluir);
                        } else {
                            System.out.println("Exclusão cancelada.");
                        }
                    }
                }
                else if (op == 0) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void montarFichaFluxo() {
        System.out.print("\nCPF do Aluno: ");
        String cpf = scanner.nextLine();
        Aluno alunoEncontrado = alunoDAO.buscarPorCpf(cpf);

        if (alunoEncontrado == null) {
            System.out.println("❌ Aluno não encontrado com esse CPF. Ficha cancelada.");
            return;
        }

        System.out.print("Semana Referência (ex: 10/04 a 17/04): ");
        String semana = scanner.nextLine();
        System.out.print("Avisos Médicos/Restrições: ");
        String obs = scanner.nextLine();

        Ficha ficha = new Ficha();
        ficha.setAlunoId(alunoEncontrado.getId());
        ficha.setSemana(semana);
        ficha.setObservacoesMedicas(obs);

        System.out.println("\n--- Avaliação Corporal ---");
        System.out.print("Peso Atual (kg): ");
        ficha.setPeso(Double.parseDouble(scanner.nextLine().replace(",", ".")));
        System.out.print("Percentual de Gordura (%): ");
        ficha.setPercentualGordura(Double.parseDouble(scanner.nextLine().replace(",", ".")));
        System.out.print("Massa Magra (kg): ");
        ficha.setMassaMagra(Double.parseDouble(scanner.nextLine().replace(",", ".")));

        System.out.println("\n--- Exercícios Disponíveis ---");
        List<Exercicio> exercicios = exercicioDAO.listarTodos();
        for (Exercicio ex : exercicios) {
            System.out.printf("ID: %d | %s (%s)\n", ex.getId(), ex.getNomePopular(), ex.getParteCorpo());
        }

        Set<String> diasDeTreino = new HashSet<>();
        int contadorExercicios = 1;

        while (true) {
            System.out.println("\nAdicionar exercício " + contadorExercicios + " na ficha? (s/n)");
            if (scanner.nextLine().equalsIgnoreCase("n")) break;

            System.out.print("Dia da semana (ex: Segunda, Terca): ");
            String dia = scanner.nextLine().toUpperCase();

            // Regra: Bloqueia se tentar adicionar um 7º dia diferente na semana
            if (!diasDeTreino.contains(dia) && diasDeTreino.size() >= 6) {
                System.out.println("⚠️ Limite de 6 dias de treino por semana atingido!");
                System.out.println("Você só pode adicionar mais exercícios nos dias: " + diasDeTreino);
                continue;
            }

            System.out.print("ID do Exercício: ");
            int exId = Integer.parseInt(scanner.nextLine());
            System.out.print("Séries: ");
            int series = Integer.parseInt(scanner.nextLine());
            System.out.print("Repetições: ");
            int reps = Integer.parseInt(scanner.nextLine());
            System.out.print("Carga Inicial (kg): ");
            double carga = Double.parseDouble(scanner.nextLine().replace(",", "."));

            diasDeTreino.add(dia);
            ficha.adicionarItem(new ItemFicha(exId, dia, series, reps, carga));
            contadorExercicios++;
        }

        if (!ficha.getItens().isEmpty()) {
            fichaDAO.salvarFicha(ficha);
            System.out.println("✅ Ficha salva com sucesso!");
        } else {
            System.out.println("Ficha cancelada.");
        }
    }

    private void menuAluno() {
        System.out.print("\nDigite seu CPF para acessar seu treino: ");
        String cpf = scanner.nextLine();
        Aluno alunoEncontrado = alunoDAO.buscarPorCpf(cpf);

        if (alunoEncontrado != null) {
            System.out.println("Olá, " + alunoEncontrado.getNome() + "! Carregando seu treino...");
            Ficha ficha = fichaDAO.buscarFichaRecentePorAluno(alunoEncontrado.getId());
            mostrarFichaFormatada(ficha);
        } else {
            System.out.println("❌ CPF não cadastrado no sistema.");
        }
    }

    private void mostrarFichaFormatada(Ficha fichaCompleta) {
        if (fichaCompleta != null) {
            Aluno aluno = alunoDAO.listarTodos().stream()
                    .filter(a -> a.getId() == fichaCompleta.getAlunoId())
                    .findFirst().orElse(null);

            System.out.println("\n==================================");
            System.out.println("    FICHA DETALHADA DO ALUNO");
            System.out.println("==================================");
            if (aluno != null) {
                System.out.println("ALUNO: " + aluno.getNome());
                System.out.println("CPF: " + aluno.getCpf());
                System.out.println("ALTURA: " + (aluno.getAltura() / 100.0) + " m");
            }
            System.out.println("Período: " + fichaCompleta.getSemana());

            System.out.println("\n--- Avaliação Corporal ---");
            System.out.printf("Peso: %.1f kg | Gordura: %.1f%% | Massa Magra: %.1f kg\n",
                    fichaCompleta.getPeso(), fichaCompleta.getPercentualGordura(), fichaCompleta.getMassaMagra());

            String obs = fichaCompleta.getObservacoesMedicas();
            System.out.println("Restrições: " + (obs == null || obs.isEmpty() ? "Nenhuma" : obs));
            System.out.println("----------------------------------");

            // Ordenando a lista de exercícios baseado nos dias da semana
            List<String> ordemDias = Arrays.asList(
                    "SEGUNDA", "TERÇA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SÁBADO", "SABADO", "DOMINGO"
            );

            fichaCompleta.getItens().sort((item1, item2) -> {
                int peso1 = ordemDias.indexOf(item1.getDiaSemana().toUpperCase());
                int peso2 = ordemDias.indexOf(item2.getDiaSemana().toUpperCase());

                if (peso1 == -1) peso1 = 99;
                if (peso2 == -1) peso2 = 99;

                return Integer.compare(peso1, peso2);
            });

            String diaAtual = "";
            for (ItemFicha item : fichaCompleta.getItens()) {
                if (!item.getDiaSemana().toUpperCase().equalsIgnoreCase(diaAtual)) {
                    System.out.println("\n[" + item.getDiaSemana().toUpperCase() + "]");
                    diaAtual = item.getDiaSemana().toUpperCase();
                }
                System.out.printf(" - %s -> %d x %d (Carga: %.1f kg)\n",
                        item.getNomeExercicio(), item.getSeries(), item.getRepeticoes(), item.getCarga());
            }
            System.out.println("==================================\n");
        } else {
            System.out.println("Nenhuma ficha encontrada.");
        }
    }
}