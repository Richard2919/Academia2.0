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

public class MenuTerminal {
    private Scanner scanner = new Scanner(System.in);
    private AlunoDAO alunoDAO = new AlunoDAO();
    private ExercicioDAO exercicioDAO = new ExercicioDAO();
    private FichaDAO fichaDAO = new FichaDAO();

    public void iniciar() {
        while (true) {
            System.out.println("\n=== SISTEMA ACADEMIA ===");
            System.out.println("1. Acesso Professor");
            System.out.println("2. Acesso Aluno");
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
            System.out.println("3. Montar Nova Ficha");
            System.out.println("4. Listar Alunos (Controle de Pagamento)");
            System.out.println("5. Listar Fichas Criadas (Resumo)");
            System.out.println("6. Ver Ficha Completa de um Aluno");
            System.out.println("7. Renovar Mensalidade de um Aluno"); // A NOVA OPÇÃO 7
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int op = Integer.parseInt(scanner.nextLine());

            if (op == 1) {
                System.out.print("Nome do Aluno: ");
                String nome = scanner.nextLine();
                // Calcula a data de vencimento para exatamente 1 mês a partir de hoje
                java.time.LocalDate vencimento = java.time.LocalDate.now().plusMonths(1);
                alunoDAO.salvar(new Aluno(nome, vencimento));
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
                    // O Java decide o status dinamicamente!
                    String status = a.isPagamentoAtrasado() ? "ATRASADO ❌" : "EM DIA ✅";
                    System.out.printf("ID: %d | Nome: %s | Vence em: %s | Status: %s\n",
                            a.getId(), a.getNome(), a.getDataVencimento(), status);
                }
            }
            else if (op == 5) {
                // OPÇÃO 5: Apenas um resumo de todas as fichas para visualização rápida
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
                // OPÇÃO 6: Visualização detalhada de uma ficha específica (por aluno)
                System.out.print("\nDigite o ID do Aluno para ver a ficha completa: ");
                int idAlunoBusca = Integer.parseInt(scanner.nextLine());
                Ficha fichaCompleta = fichaDAO.buscarFichaRecentePorAluno(idAlunoBusca);

                if (fichaCompleta != null) {
                    System.out.println("\n==================================");
                    System.out.println("    FICHA DETALHADA DO ALUNO");
                    System.out.println("==================================");
                    System.out.println("Período: " + fichaCompleta.getSemana());

                    String obs = fichaCompleta.getObservacoesMedicas();
                    System.out.println("Atenção Médica: " + (obs == null || obs.isEmpty() ? "Nenhuma" : obs));
                    System.out.println("----------------------------------");

                    String diaAtual = "";
                    for (ItemFicha item : fichaCompleta.getItens()) {
                        if (!item.getDiaSemana().equalsIgnoreCase(diaAtual)) {
                            System.out.println("\n[" + item.getDiaSemana().toUpperCase() + "]");
                            diaAtual = item.getDiaSemana();
                        }
                        System.out.printf(" - %s -> %d séries de %d reps (Carga: %.1f kg)\n",
                                item.getNomeExercicio(), item.getSeries(), item.getRepeticoes(), item.getCarga());
                    }
                    System.out.println("==================================\n");
                } else {
                    System.out.println("O aluno não possui nenhuma ficha montada.");
                }
            }
            else if (op == 7) {
                // OPÇÃO 7: Renovar a mensalidade de um aluno
                System.out.print("\nDigite o ID do Aluno que está pagando a mensalidade: ");
                int idRenovar = Integer.parseInt(scanner.nextLine());

                // Chamamos o método no AlunoDAO para atualizar o banco
                alunoDAO.renovarMensalidade(idRenovar);
            }
            else if (op == 0) {
                break;
            }
        }
    }

    private void montarFichaFluxo() {
        System.out.print("\nID do Aluno: ");
        int alunoId = Integer.parseInt(scanner.nextLine());
        System.out.print("Semana Referência (ex: 10/04 a 17/04): ");
        String semana = scanner.nextLine();
        System.out.print("Avisos Médicos/Restrições: ");
        String obs = scanner.nextLine();

        Ficha ficha = new Ficha();
        ficha.setAlunoId(alunoId);
        ficha.setSemana(semana);
        ficha.setObservacoesMedicas(obs);

        // Lista os exercícios disponíveis para ajudar o professor
        System.out.println("\n--- Exercícios Disponíveis ---");
        List<Exercicio> exercicios = exercicioDAO.listarTodos();
        for (Exercicio ex : exercicios) {
            System.out.printf("ID: %d | %s (%s)\n", ex.getId(), ex.getNomePopular(), ex.getParteCorpo());
        }

        while (true) {
            System.out.println("\nAdicionar exercício na ficha? (s/n)");
            if (scanner.nextLine().equalsIgnoreCase("n")) break;

            System.out.print("ID do Exercício: ");
            int exId = Integer.parseInt(scanner.nextLine());
            System.out.print("Dia da semana (ex: Segunda): ");
            String dia = scanner.nextLine();
            System.out.print("Séries: ");
            int series = Integer.parseInt(scanner.nextLine());
            System.out.print("Repetições: ");
            int reps = Integer.parseInt(scanner.nextLine());
            System.out.print("Carga Inicial (kg): ");
            double carga = Double.parseDouble(scanner.nextLine());

            // Adiciona o item dentro do objeto ficha, mas ainda não salva no banco
            ficha.adicionarItem(new ItemFicha(exId, dia, series, reps, carga));
        }

        // Se a ficha tiver itens, envia o objeto completo para o DAO salvar tudo de uma vez
        if (!ficha.getItens().isEmpty()) {
            fichaDAO.salvarFicha(ficha);
        } else {
            System.out.println("Ficha cancelada: nenhum exercício foi adicionado.");
        }
    }

    private void menuAluno() {
        System.out.print("\nDigite seu ID de Aluno: ");
        int id = Integer.parseInt(scanner.nextLine());
        Ficha ficha = fichaDAO.buscarFichaRecentePorAluno(id);

        if (ficha != null) {
            System.out.println("\n==================================");
            System.out.println("       TREINO DA SEMANA");
            System.out.println("==================================");
            System.out.println("Período: " + ficha.getSemana());

            String obs = ficha.getObservacoesMedicas();
            System.out.println("Atenção Médica: " + (obs == null || obs.isEmpty() ? "Nenhuma" : obs));
            System.out.println("----------------------------------");

            String diaAtual = "";
            for (ItemFicha item : ficha.getItens()) {
                // Quebra visual por dia da semana
                if (!item.getDiaSemana().equalsIgnoreCase(diaAtual)) {
                    System.out.println("\n[" + item.getDiaSemana().toUpperCase() + "]");
                    diaAtual = item.getDiaSemana();
                }
                System.out.printf(" - %s -> %d séries de %d reps (Carga: %.1f kg)\n",
                        item.getNomeExercicio(), item.getSeries(), item.getRepeticoes(), item.getCarga());
            }
            System.out.println("==================================\n");
        } else {
            System.out.println("Nenhuma ficha encontrada para este ID.");
        }
    }
}